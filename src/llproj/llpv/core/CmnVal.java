package llproj.llpv.core;

import java.awt.Font;

import org.json.JSONObject;

import llproj.llpv.util.MessageUt;

public class CmnVal {
	public static final String process_title = "llprocessView";
	public static final String Version = "v1.0";
	public static String homepage = "www.llpv.kr";
	public static final String url = "https://llload.com:9995/rest";
	public static final String split_str = "_#_@!@_#_";
	public static int cycle_sec = 10;
	public static int send_data_cycle_sec = 60;
	public static int update_note_cycle_sec = 10;
	public static int rank_combo_box1_1_idx = 0;
	public static int rank_combo_box1_2_idx = 0;
	public static int note_rows = 999;
	public static boolean is_rank_changed = false;
	public static boolean is_use_alarm = false;
	public static boolean is_use_limit = false;
	public static String alarm_run = "50";
	public static String alarm_rest = "10";
	public static String rank_from_time = "";
	public static String rank_to_time = "";
	public static JSONObject limitList = new JSONObject();

	public static Font font = new Font(MessageUt.getMessage("font"), Font.PLAIN, 12);
	public static Font font_b = new Font(MessageUt.getMessage("font"), Font.PLAIN, 12);

	public static String[] dates = new String[note_rows];
}
