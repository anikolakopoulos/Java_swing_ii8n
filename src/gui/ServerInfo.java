package gui;

class ServerInfo {
	private String name;
	private int id;
	private String date;
	private int hour;
	private int minute;
	private int second;
	private int AM_PM;
	private String AM;
	private String PM;
	private String AM_PMValue;
	private boolean checked;

	public ServerInfo(String name, int id, String date, int hour, int minute, int second, String AM_PMValue, boolean checked) {
		this.name = name;
		this.id = id;
		this.date = date;
		this.hour = hour;
		this.minute = minute;
		this.second = second;
		this.AM_PMValue = AM_PMValue;
		this.checked = checked;
	}

	public ServerInfo(String date) {
		this.date = date;
	}

	public int getId() {
		return id;
	}

	public int getAM_PM(int AM_PM) {
		return AM_PM;
	}

	public String getAM_PMValue(int AM_PM) {
		if (AM_PM == 1) {
			AM_PMValue = PM;
			System.out.println(AM_PMValue);
		} else {
			AM_PMValue = AM;
		}
		return AM_PMValue;
	}

	public String toString() {
		// return id + ": " + name;
		return name + " (" + date + "  " + hour + ":" + minute + ":" + second
				+ " " + AM_PMValue + ")";
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public boolean isChecked() {
		return checked;
	}

}