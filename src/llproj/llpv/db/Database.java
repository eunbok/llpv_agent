package llproj.llpv.db;

import java.awt.TrayIcon;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import llproj.llpv.ServerStart;
import llproj.llpv.core.CmnVal;
import llproj.llpv.util.CmnUt;
import llproj.llpv.util.MessageUt;
import llproj.llpv.vo.DataVO;

public class Database {
	private static final Logger log = Logger.getLogger(Database.class);
	String url;
	String id;
	String pass;

	public Database(String url, String id, String pass) {
		this.url = url;
		this.id = id;
		this.pass = pass;
	}

	public void createDB() {
		Connection con = null;
		Statement stmt = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now_dt = sdf.format(new Date());

		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS t_data(";
			sql += "run_file varchar(100),";
			sql += "run_title varchar(100),";
			sql += "run_sec int,";
			sql += "_datetime datetime,";
			sql += "stored_time datetime";
			sql += ");";
			stmt.execute(sql.toString());

			sql = "CREATE TABLE IF NOT EXISTS t_config(";
			sql += "config_code varchar(100),";
			sql += "config_value varchar(100)";
			sql += ");";
			stmt.execute(sql.toString());

			sql = "CREATE TABLE IF NOT EXISTS t_limit(";
			sql += "limit_id varchar(220),";
			sql += "run_file varchar(100),";
			sql += "run_title varchar(100),";
			sql += "run_sec int,";
			sql += "limit_min int,";
			sql += "_datetime datetime,";
			sql += "PRIMARY KEY  (limit_id)";
			sql += ");";
			stmt.execute(sql.toString());

			sql = "CREATE TABLE IF NOT EXISTS t_note(";
			sql += "note_id int,";
			sql += "note_dt CLOB,";
			sql += "note_text CLOB,";
			sql += "PRIMARY KEY  (note_id)";
			sql += ");";
			stmt.execute(sql.toString());

			sql = "SELECT config_value ";
			sql += "FROM t_config ";
			sql += "WHERE config_code = 'start_dt';";
			ResultSet rs = stmt.executeQuery(sql);
			boolean is_first = true;
			if (rs != null) {
				rs.last(); // moves cursor to the last row
				is_first = rs.getRow() == 0 ? true : false; // get row id
			}
			if (is_first) {
				sql = "INSERT INTO t_config (config_code, config_value) VALUES ('start_dt', '" + now_dt + "');";
				stmt.execute(sql.toString());

				sql = "INSERT INTO t_config (config_code, config_value) VALUES ('running_dt', '"
						+ now_dt.substring(0, 10) + "');";
				stmt.execute(sql.toString());

				sql = "INSERT INTO t_config (config_code, config_value) VALUES ('is_use_limit', 'N');";
				stmt.execute(sql.toString());

				sql = "INSERT INTO t_config (config_code, config_value) VALUES ('is_send_data', 'N');";
				stmt.execute(sql.toString());

				sql = "INSERT INTO t_config (config_code, config_value) VALUES ('is_use_alarm', 'N');";
				stmt.execute(sql.toString());

				sql = "INSERT INTO t_config (config_code, config_value) VALUES ('alarm_run', '" + CmnVal.alarm_run
						+ "');";
				stmt.execute(sql.toString());

				sql = "INSERT INTO t_config (config_code, config_value) VALUES ('alarm_rest', '" + CmnVal.alarm_rest
						+ "');";
				stmt.execute(sql.toString());

				sql = "INSERT INTO t_note (note_id, note_dt, note_text) VALUES (1, '', '');";
				stmt.execute(sql.toString());

			}

			// show config
			log.info("[show config map]");
			sql = "select * from t_config";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String config_code = rs.getString("config_code");
				String config_value = rs.getString("config_value");
				log.info("config_code:" + String.format("%20s", config_code) + "  ||  " + "config_value:"
						+ String.format("%20s", config_value));
			}
			log.info("---------------------------");

			log.info("[show limit list]");
			if (!getConfig("running_dt").equals(now_dt.substring(0, 10))) {
				resetLimitRunSec();
			}

			sql = "select * from t_limit";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				JSONObject jo = new JSONObject();
				String key = rs.getString("limit_id");
				String run_file = rs.getString("run_file");
				String run_title = rs.getString("run_title");
				int run_sec = rs.getInt("run_sec");
				int limit_min = rs.getInt("limit_min");
				jo.put("run_file", run_file);
				jo.put("run_title", run_title);
				jo.put("run_sec", run_sec);
				jo.put("limit_min", limit_min);
				CmnVal.limitList.put(key, jo);
			}
			log.info(CmnVal.limitList.toString());

			log.info("---------------------------");

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void insert(DataVO dv) {
		Connection con = null;
		PreparedStatement pstmt = null;
		try {
			if("Unknown File".equals(dv.getRun_file())) {
				log.debug("no save Unknown File");
				return;
			}
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);
			String sql = "insert into t_data(run_file, run_title, run_sec, _datetime, stored_time) values(?, ?, ?, ?, ?)";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, dv.getRun_file().length() < 100 ? dv.getRun_file() : dv.getRun_file().substring(0, 49));
			pstmt.setString(2,
					dv.getRun_title().length() < 100 ? dv.getRun_title() : dv.getRun_title().substring(0, 49));
			pstmt.setInt(3, dv.getRun_sec());
			pstmt.setString(4, dv.get_datetime());
			pstmt.setString(5, dv.getStored_time());

			int r = pstmt.executeUpdate();
			log.debug("[Data insert] " + dv.get_datetime());
//			log.debug("run_file:" + dv.getRun_file());
//			log.debug("run_title:" + dv.getRun_title());
//			log.debug("_datetime:" + dv.get_datetime());
//			log.debug("[SQL Result : " + r + "]");

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getRank(ArrayList<DataVO> data_arr) {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);
			String sql = "";
			String where = "";
			String select = "";
			String group_by = "";
			String order_by = "";
			String from = "FROM t_data ";
			String limit = "limit 10";
			if (CmnVal.rank_combo_box1_1_idx == 0) {
				select = "SELECT run_file, sum(run_sec) as total_cnt ";
				group_by = "group by run_file ";
				order_by = "order by total_cnt desc, run_file asc ";
			} else if (CmnVal.rank_combo_box1_1_idx == 1) {
				select = "SELECT run_file, run_title, sum(run_sec) as total_cnt ";
				group_by = "group by run_file, run_title ";
				order_by = "order by total_cnt desc, run_title asc ";
			}

			if (CmnVal.rank_combo_box1_2_idx == 0) {

				where = "where _datetime > FORMATDATETIME('" + ServerStart.TODAY_STR + "','yyyy-MM-dd HH:mm:ss') ";
			} else if (CmnVal.rank_combo_box1_2_idx == 1) {
				where = "where _datetime > DATEADD(DAY,-1,CURRENT_DATE) ";
			} else if (CmnVal.rank_combo_box1_2_idx == 2) {
				where = "where FORMATDATETIME(_datetime,'yyyy-MM-dd') = FORMATDATETIME(CURRENT_DATE,'yyyy-MM-dd') ";
			} else if (CmnVal.rank_combo_box1_2_idx == 3) {
				where = "where _datetime > DATEADD(DAY,-7,CURRENT_DATE) ";
			} else if (CmnVal.rank_combo_box1_2_idx == 4) {
				where = "where _datetime > DATEADD(MONTH,-1,CURRENT_DATE) ";
			} else {
				where = "where _datetime >= FORMATDATETIME('" + CmnVal.rank_from_time
						+ "','yyyy-MM-dd HH:mm:ss') and _datetime <= FORMATDATETIME('" + CmnVal.rank_to_time
						+ "','yyyy-MM-dd HH:mm:ss')";
			}

			sql = select + from + where + group_by + order_by + limit;

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				DataVO dv = new DataVO();

				if (CmnVal.rank_combo_box1_1_idx == 0) {
					String run_file = rs.getString("run_file");
					int total_cnt = rs.getInt("total_cnt");
					dv.setRun_file(run_file);
					dv.setTotal_cnt(total_cnt);
					data_arr.add(dv);
				} else {
					String run_file = rs.getString("run_file");
					String run_title = rs.getString("run_title");
					int total_cnt = rs.getInt("total_cnt");
					dv.setRun_file(run_file);
					dv.setRun_title(run_title);
					dv.setTotal_cnt(total_cnt);
					data_arr.add(dv);
				}
			}

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getSendData(ArrayList<DataVO> data_arr, String startDate, String endDate) {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);
			String sql = "";
			sql = "SELECT run_file, run_title, _datetime, run_sec, stored_time from t_data ";
			sql += "where stored_time > '" + startDate + "' and stored_time <= '" + endDate + "';";
			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				DataVO dv = new DataVO();
				String run_file = rs.getString("run_file");
				String run_title = rs.getString("run_title");
				String _datetime = rs.getString("_datetime");
				String stored_time = rs.getString("stored_time");
				int run_sec = rs.getInt("run_sec");

				dv.setRun_file(run_file);
				dv.setRun_title(run_title);
				dv.setRun_sec(run_sec);
				dv.set_datetime(_datetime);
				dv.setStored_time(stored_time);
				data_arr.add(dv);
			}

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getSearch(DefaultTableModel model, String text, String start_date, String end_date) {

		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);
			String sql = "";
			String where = "";
			String select = "";
			String group_by = "";
			String order_by = "";
			String from = "FROM t_data ";

			select = "SELECT run_file, run_title, _datetime, sum(run_sec) as total_cnt ";
			group_by = "group by run_file, run_title, _datetime ";
			order_by = "order by _datetime desc ";

			where = "where _datetime >= '" + start_date + "' and _datetime <= '" + end_date
					+ "' and (lower(run_file) like lower('%" + text + "%') or lower(run_title) like lower('%" + text
					+ "%'))";

			sql = select + from + where + group_by + order_by;

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String run_file = rs.getString("run_file");
				String run_title = rs.getString("run_title");
				String _datetime = rs.getString("_datetime");
				String total_cnt = CmnUt.secToTime(Integer.parseInt(rs.getString("total_cnt")));

				model.addRow(new Object[] { run_file, run_title, _datetime, total_cnt });
			}

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public String getConfig(String config_code) {
		Connection con = null;
		Statement stmt = null;
		String result = "";
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "SELECT config_value ";
			sql += "FROM t_config ";
			sql += "WHERE config_code = '" + config_code + "';";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				result = rs.getString("config_value");
			}
		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public void setConfig(String config_code, String value) {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "update t_config set config_value = '" + value + "' where config_code = '" + config_code
					+ "';";
			stmt.execute(sql);

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deleteDB() {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "delete from t_data;";
			stmt.execute(sql);

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getLimitList(DefaultTableModel model) {

		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);
			String sql = "";
			String where = "";
			String select = "";
			String group_by = "";
			String order_by = "";
			String from = "FROM t_limit ";

			select = "SELECT limit_id, run_file, run_title, _datetime, run_sec, limit_min ";
			group_by = "";
			order_by = "order by _datetime desc ";

			where = "";

			sql = select + from + where + group_by + order_by;

			stmt = con.createStatement();

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				String limit_id = rs.getString("limit_id");
				String run_file = rs.getString("run_file");
				String run_title = rs.getString("run_title");
				String run_sec = CmnUt.secToTime(rs.getInt("run_sec"));
				String limit_min = CmnUt.secToTime(rs.getInt("limit_min") * 60);
				String _datetime = rs.getString("_datetime");
				model.isCellEditable(0, 1);
				model.addRow(new Object[] { run_file, run_title, run_sec, limit_min, _datetime, limit_id });
			}

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void saveLimit(String run_file, String run_title, String limit_min) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String now_dt = sdf.format(new Date());

		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String limit_id = run_file + CmnVal.split_str + run_title;
			String sql = "INSERT INTO t_limit (limit_id, run_file, run_title, run_sec, limit_min, _datetime) VALUES "
					+ "('" + limit_id + "', '" + run_file + "', '" + run_title + "', 0, " + limit_min + ", '" + now_dt
					+ "');";
			stmt.execute(sql);

			JSONObject jo = new JSONObject();
			jo.put("run_file", run_file);
			jo.put("run_title", run_title);
			jo.put("run_sec", 0);
			jo.put("limit_min", limit_min);
			CmnVal.limitList.put(limit_id, jo);

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
			if (e.getMessage().contains("primary key")) {
				ServerStart.trayIcon.displayMessage(MessageUt.getMessage("tray"), MessageUt.getMessage("tray.limit.exist_rule"), TrayIcon.MessageType.INFO);
			}
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void deleteLimit(String value) {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "delete from t_limit where limit_id='" + value + "';";
			stmt.execute(sql);

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public void resetLimitRunSec() {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "update t_limit set run_sec = 0;";
			stmt.execute(sql);

			Iterator iter = CmnVal.limitList.keys();
			while (iter.hasNext()) {
				CmnVal.limitList.getJSONObject(iter.next().toString()).put("run_sec", 0);
			}
			log.info("reset limit run_sec");

		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateLimitRunSec(String limit_id, int sec) {
		// TODO Auto-generated method stub
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "update t_limit set run_sec = " + sec + " where limit_id = '" + limit_id + "';";
			stmt.execute(sql);
		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateNote(String note_dt, String note_text) {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();

			String sql = "update t_note set note_dt = '" + note_dt + "', note_text = '" + CmnUt.encode(note_text)
					+ "' where note_id = '1';";
			stmt.execute(sql);
		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void getNote(JTextArea jTextArea5_2) {
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.h2.Driver");
			con = DriverManager.getConnection("jdbc:h2:file:" + url, id, pass);

			stmt = con.createStatement();
			String sql = "SELECT note_dt, note_text ";
			sql += "FROM t_note ";
			sql += "WHERE note_id = 1;";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String[] split = rs.getString("note_dt").split("\n");
				for (int i = 0; i < split.length; i++) {
					CmnVal.dates[i] = split[i];
				}
				jTextArea5_2.setText(CmnUt.decode(rs.getString("note_text")));
			}
		} catch (SQLException e) {
			log.error("[SQL Error : " + e.getMessage() + "]");
		} catch (ClassNotFoundException e1) {
			log.error("[JDBC Connector Driver error : " + e1.getMessage() + "]");
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
