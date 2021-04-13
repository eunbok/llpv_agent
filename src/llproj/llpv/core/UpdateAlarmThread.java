package llproj.llpv.core;

import java.awt.TrayIcon;
import java.awt.Window;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import llproj.llpv.ServerStart;
import llproj.llpv.util.CmnUt;

public class UpdateAlarmThread implements Runnable {
	private static final Logger log = Logger.getLogger(UpdateAlarmThread.class);
//	JTabbedPane panel;
	JLabel label1;
	JLabel label2;
	JTabbedPane t;
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	Date alarm_run_dt, alarm_rest_dt, now_dt, run_dt;
	boolean is_first = true;
	boolean is_run = false;

	public UpdateAlarmThread(JTabbedPane t, JLabel label1, JLabel label2) {
		this.t = t;
		this.label1 = label1;
		this.label2 = label2;
	}

	public void run() {
		try {

			while (true) {
				Window window = SwingUtilities.getWindowAncestor(t);
				JFrame frame = (JFrame) window;
				now_dt = new Date();
				long now_dt_gt = now_dt.getTime();
				long today_start_dt_gt = ServerStart.TODAY.getTime();
				String running_dt = CmnUt.secToTime((int) ((now_dt_gt - today_start_dt_gt) / 1000));
				label1.setText(running_dt);

				if (CmnVal.is_use_alarm) {
					if (is_first) {
						alarm_run_dt = new Date();
						alarm_rest_dt = new Date(alarm_run_dt.getTime());
						alarm_run_dt.setMinutes(alarm_run_dt.getMinutes() + Integer.parseInt(CmnVal.alarm_run));
						alarm_rest_dt.setMinutes(alarm_rest_dt.getMinutes() + Integer.parseInt(CmnVal.alarm_run)
								+ Integer.parseInt(CmnVal.alarm_rest));
						is_first = false;
						is_run = true;
					}
					String check_run_time = sdf.format(alarm_run_dt);
					String check_rest_time = sdf.format(alarm_rest_dt);
					String now_time = sdf.format(now_dt);

					if (is_run) {
						label2.setText(
								"남은 사용시간 : " + CmnUt.secToTime((int) ((alarm_run_dt.getTime() - now_dt_gt) / 1000)));
					} else {
						label2.setText(
								"남은 휴식시간 : " + CmnUt.secToTime((int) ((alarm_rest_dt.getTime() - now_dt_gt) / 1000)));
					}

					if (check_run_time.equals(now_time)) {
						log.info("사용시간 경과 | 휴식 시작 알림");
						frame.setState(frame.NORMAL);
						// 윈도우 표시
						frame.setVisible(true);
						// 윈도우 포커싱
						if (frame.getFocusableWindowState()) {
							frame.requestFocus();
						}
						
						String cmd = "rundll32 user32.dll, LockWorkStation";
						Runtime.getRuntime().exec(cmd);
						
						ServerStart.trayIcon.displayMessage("llpv 알림", "사용시간 경과 | 휴식 시작", TrayIcon.MessageType.INFO);
						alarm_run_dt.setMinutes(alarm_run_dt.getMinutes() + Integer.parseInt(CmnVal.alarm_run)
								+ Integer.parseInt(CmnVal.alarm_rest));
						is_run = false;
					}

					if (check_rest_time.equals(now_time)) {
						log.info("휴식 종료 | 사용 시작 알림");
						if (!"0".equals(CmnVal.alarm_rest)) {
							frame.setState(frame.NORMAL);
							// 윈도우 표시
							frame.setVisible(true);
							// 윈도우 포커싱
							if (frame.getFocusableWindowState()) {
								frame.requestFocus();
							}
							ServerStart.trayIcon.displayMessage("llpv 알림", "휴식 종료 | 사용 시작", TrayIcon.MessageType.INFO);
						}
						alarm_rest_dt.setMinutes(alarm_rest_dt.getMinutes() + Integer.parseInt(CmnVal.alarm_run)
								+ Integer.parseInt(CmnVal.alarm_rest));
						is_run = true;
					}

				} else {
					is_first = true;
				}

				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}