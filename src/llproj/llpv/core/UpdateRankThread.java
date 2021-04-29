package llproj.llpv.core;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JLabel;

import org.apache.log4j.Logger;

import llproj.llpv.db.Database;
import llproj.llpv.util.CmnUt;
import llproj.llpv.util.MessageUt;
import llproj.llpv.vo.DataVO;

public class UpdateRankThread implements Runnable {
  private static final Logger log = Logger.getLogger(UpdateRankThread.class);
  Database db;
  JLabel label[];
  SimpleDateFormat sdf = new SimpleDateFormat("ss");

  public UpdateRankThread(Database db, JLabel label[]) {
    this.db = db;
    this.label = label;
  }

  public void run() {
    try {
      while (true) {
        int _sec = Integer.parseInt(sdf.format(new Date()));
        if (_sec % CmnVal.cycle_sec == 0 || CmnVal.is_rank_changed) {
          log.trace("[UpdateProcessRank]");

          ArrayList data_arr = new ArrayList<DataVO>();
          db.getRank(data_arr);
          for (int i = 0; i < 10; i++) {
            label[i].setText("");
          }

          if (data_arr.size() == 0) {
            label[0].setText(MessageUt.getMessage("rank.info"));
          }

          for (int i = 0; i < data_arr.size(); i++) {
            DataVO dv = (DataVO) data_arr.get(i);

            if (CmnVal.rank_combo_box1_1_idx == 0) {
              label[i].setText(i + 1 + ". " + dv.getRun_file() + " " + "("
                  + CmnUt.secToTime(dv.getTotal_cnt()) + ")");

            } else {
              label[i].setText(i + 1 + ". " + dv.getRun_title() + " - " + dv.getRun_file() + " "
                  + "(" + CmnUt.secToTime(dv.getTotal_cnt()) + ")");
            }

          }
          CmnVal.is_rank_changed = false;
        }
        Thread.sleep(1000);
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
