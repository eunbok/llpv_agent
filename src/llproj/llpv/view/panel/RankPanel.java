package llproj.llpv.view.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import llproj.llpv.core.CmnVal;
import llproj.llpv.core.UpdateRankThread;
import llproj.llpv.db.Database;
import llproj.llpv.util.CmnUt;
import llproj.llpv.vo.DataVO;

public class RankPanel extends JPanel {
	public RankPanel(Database db) {
		JPanel rankTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
		JPanel rankBottom = new JPanel(new GridLayout(10, 1));
		rankTop.setPreferredSize(new Dimension(850, 50));
		rankBottom.setPreferredSize(new Dimension(800, 300));

		JComboBox<String> viewComboBox = new JComboBox<>();
		JComboBox<String> timeComboBox = new JComboBox<>();

		JLabel splitTimeText = new JLabel("~"); // 시간지정
		splitTimeText.setFont(CmnVal.font);
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JFormattedTextField startTimeTextField = new JFormattedTextField(sdf);
		JFormattedTextField endTimeTextField = new JFormattedTextField(sdf);

		viewComboBox.setFont(CmnVal.font);
		viewComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "프로세스명", "프로그램 제목" }));
		viewComboBox.setBackground(Color.white);
		viewComboBox.setFocusable(false);
		viewComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CmnVal.is_rank_changed = true;
				CmnVal.rank_combo_box1_1_idx = viewComboBox.getSelectedIndex();
			}
		});

		JLabel lists[] = new JLabel[10];

		ArrayList<DataVO> data_arr;
		data_arr = new ArrayList<DataVO>();
		db.getRank(data_arr);
		for (int i = 0; i < data_arr.size(); i++) {
			DataVO dv = data_arr.get(i);

			lists[i].setText(i + 1 + ". " + dv.getRun_file() + " " + "(" + CmnUt.secToTime(dv.getTotal_cnt()) + ")");
		}

		timeComboBox.setFont(CmnVal.font);
		timeComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "실행시간", "24시간", "오늘", "일주일", "한달", "시간지정" }));
		timeComboBox.setBackground(Color.white);
		timeComboBox.setFocusable(false);
		timeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				CmnVal.is_rank_changed = true;
				CmnVal.rank_combo_box1_2_idx = timeComboBox.getSelectedIndex();
				if (CmnVal.rank_combo_box1_2_idx == 5) {
					CmnVal.rank_from_time = startTimeTextField.getText();
					CmnVal.rank_to_time = endTimeTextField.getText();
					startTimeTextField.setVisible(true);
					splitTimeText.setVisible(true);
					endTimeTextField.setVisible(true);
				} else {
					startTimeTextField.setVisible(false);
					splitTimeText.setVisible(false);
					endTimeTextField.setVisible(false);
				}
			}

		});

		startTimeTextField.setVisible(false);
		splitTimeText.setVisible(false);
		endTimeTextField.setVisible(false);

		Calendar startDt = Calendar.getInstance();
		startDt.add(Calendar.DATE, -1);
		Calendar endDt = Calendar.getInstance();
		endDt.add(Calendar.MINUTE, 1);
		startTimeTextField.setValue(startDt.getTime());
		endTimeTextField.setValue(endDt.getTime());

		rankTop.add(viewComboBox);
		rankTop.add(timeComboBox);
		rankTop.add(startTimeTextField);
		rankTop.add(splitTimeText);
		rankTop.add(endTimeTextField);

		for (int i = 0; i < 10; i++) {
			lists[i] = new JLabel("");
			lists[i].setFont(CmnVal.font);
			lists[i].setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			rankBottom.add(lists[i]);
		}
		lists[0].setText("시간이 지나면 자동으로 갱신됩니다. (활성화 상태 프로그램의 사용시간을 저장합니다)");

		this.add(rankTop, BorderLayout.NORTH);
		this.add(rankBottom, BorderLayout.CENTER);

		Border rankBottomBorder = new LineBorder(Color.black, 1, false);
		rankBottom.setBackground(Color.white);
		rankBottom.setBorder(rankBottomBorder);

		UpdateRankThread updateRankThread = new UpdateRankThread(db, lists);
		Thread thread = new Thread(updateRankThread);
		thread.start();
	}
}
