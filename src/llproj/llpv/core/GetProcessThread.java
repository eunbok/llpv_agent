package llproj.llpv.core;

import java.awt.TrayIcon;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import llproj.llpv.ServerStart;
import llproj.llpv.db.Database;
import llproj.llpv.vo.DataVO;

public class GetProcessThread implements Runnable {
	private static final Logger log = Logger.getLogger(GetProcessThread.class);
	Database db;

	public GetProcessThread(Database db) {
		this.db = db;
	}

	public void run() {
		try {
			DataVO firstDv = new GetProcessJob().getProgram();
			String runFile = firstDv.getRun_file();
			String runTitle = firstDv.getRun_title();
			String _datetime = firstDv.get_datetime();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			SimpleDateFormat sdf2 = new SimpleDateFormat("SSS");
			String stored_time = "";
			int sec = 1;

			DataVO dv;
			DataVO beforeDv;

			Calendar now;
			while(true){
				 now = Calendar.getInstance();
				if("000".equals(sdf2.format(now.getTime()))) {
					break;
				}
				Thread.sleep(1);
			}
 
			while (true) {
				if (now.getTime().getTime() <= new Date().getTime()) {
//					log.debug("0초에 가까이 시간이 찍히는지 확인 : "sdf1.format(new Date()));
					dv = new GetProcessJob().getProgram();

					LimitProcess(dv);

					beforeDv = new DataVO();
					stored_time = sdf.format(new Date());

					if (stored_time.substring(17, 19).equals("30")) {
						db.setConfig("running_dt", stored_time.substring(0, 10));
					}
					if (stored_time.substring(17, 19).equals("00")) {
						if (!db.getConfig("running_dt").equals(stored_time.substring(0, 10))) {
							db.resetLimitRunSec();
						}
					}

					if (runFile.equals(dv.getRun_file()) && runTitle.equals(dv.getRun_title())) {
						sec++;
						if (sec > 59) {
							dv.setRun_sec(sec);
							dv.set_datetime(_datetime);
							dv.setStored_time(stored_time);
							db.insert(dv);
							sec = 1;
						}
					} else {
						beforeDv.setRun_file(runFile);
						beforeDv.setRun_title(runTitle);
						beforeDv.setRun_sec(sec);
						beforeDv.set_datetime(_datetime);
						beforeDv.setStored_time(stored_time);
						db.insert(beforeDv);
						sec = 1;
						runFile = dv.getRun_file();
						runTitle = dv.getRun_title();
						_datetime = dv.get_datetime();
					}
					
					now.add(Calendar.SECOND, 1);
				}
				Thread.sleep(10);
			}
		} catch (

		InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void LimitProcess(DataVO dv) throws Exception {
		String run_file = dv.getRun_file();
		String run_title = dv.getRun_title();
		int pid = dv.getPid();

		Iterator iter = CmnVal.limitList.keys();
		while (iter.hasNext()) {
			String key = iter.next().toString();
			String keySplit[] = key.split(CmnVal.split_str);
			if (keySplit.length == 1) {
				if (run_file.equals(keySplit[0])) {
					updateLimitRunSec(key, run_file, run_title, pid);
				}
			} else {
				if (run_file.equals(keySplit[0]) && run_title.contains(keySplit[1])) {
					updateLimitRunSec(key, run_file, run_title, pid);
				}
			}
		}

	}

	private void updateLimitRunSec(String key, String run_file, String run_title, int pid) throws Exception {
		// TODO Auto-generated method stub
		if (CmnVal.limitList.has(key)) {
			int sec = CmnVal.limitList.getJSONObject(key).getInt("run_sec") + 1;
			int limit_min = CmnVal.limitList.getJSONObject(key).getInt("limit_min");
			CmnVal.limitList.getJSONObject(key).put("run_sec", sec);
			db.updateLimitRunSec(key, sec);

			if (CmnVal.is_use_limit) {
				if ((limit_min - 5) * 60 == sec) {
					ServerStart.trayIcon.displayMessage("llpv 알림", "[제한기능] 5분 후 '" + run_file + "' 이 종료 됩니다.",
							TrayIcon.MessageType.INFO);
					log.info("제한시간 초과로 프로그램 강제종료 = " + run_file + " - " + run_title + "(" + limit_min + "분)");
				}

				if ((limit_min - 1) * 60 == sec) {
					ServerStart.trayIcon.displayMessage("llpv 알림", "[제한기능] 1분 후 '" + run_file + "'이 종료 됩니다.",
							TrayIcon.MessageType.INFO);
					log.info("제한시간 초과로 프로그램 강제종료 = " + run_file + " - " + run_title + "(" + limit_min + "분)");
				}

				if (limit_min * 60 <= sec) {
					String cmd = "taskkill /F /PID " + pid;
					Runtime.getRuntime().exec(cmd);
					log.info("제한시간 초과로 프로그램 강제종료 = " + run_file + " - " + run_title + "(" + limit_min + "분)");
					ServerStart.trayIcon.displayMessage("llpv 알림", "[제한기능] '" + run_file + "'이 종료 됩니다.",
							TrayIcon.MessageType.INFO);
				}
			}
		}
	}
}