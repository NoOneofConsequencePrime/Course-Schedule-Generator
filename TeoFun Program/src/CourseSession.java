public class CourseSession extends Session {
	private String dayStr;
	
	public CourseSession(String dayStr, String startTimeStr, String endTimeStr) {
		super(Time.toMins(startTimeStr), Time.toMins(endTimeStr));
		this.dayStr = dayStr;
	}

	public String getDayStr() {
		return dayStr;
	}
}