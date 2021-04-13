package llproj.llpv.vo;

public class DataVO {
	private String run_file;
	private String run_title;
	private int run_sec;
	private String _datetime;
	private String stored_time;
	private int pid;
	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getStored_time() {
		return stored_time;
	}

	public void setStored_time(String stored_time) {
		this.stored_time = stored_time;
	}

	private int total_cnt;

	public String getRun_file() {
		return run_file;
	}

	public void setRun_file(String run_file) {
		this.run_file = run_file;
	}

	public String getRun_title() {
		return run_title;
	}

	public void setRun_title(String run_title) {
		this.run_title = run_title;
	}

	public String get_datetime() {
		return _datetime;
	}

	public void set_datetime(String _datetime) {
		this._datetime = _datetime;
	}
	public int getTotal_cnt() {
		return total_cnt;
	}

	public void setTotal_cnt(int total_cnt) {
		this.total_cnt = total_cnt;
	}

	public int getRun_sec() {
		return run_sec;
	}

	public void setRun_sec(int run_sec) {
		this.run_sec = run_sec;
	}

}
