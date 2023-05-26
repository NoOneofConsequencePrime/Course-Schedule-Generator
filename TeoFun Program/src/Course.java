public class Course extends Session {
	private int courseCode;
	private String courseName, instructorName;
	private String dayStr;
	
	public Course(int courseCode, String courseName, String instructorName, String dayStr, String startTimeStr, String endTimeStr) {
		super(Time.toMins(startTimeStr), Time.toMins(endTimeStr));
		this.courseCode = courseCode;
		this.courseName = courseName; this.instructorName = instructorName;
		this.dayStr = dayStr;
	}
}