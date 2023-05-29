import java.util.*;

public class Course {
	private int courseCode;
	private String courseName, instructorName;
	private double instructorScore;
	private ArrayList<CourseSession> sessionList = new ArrayList<>();
	
	public Course(int courseCode, String courseName, String instructorName, double instructorScore) {
		this.courseCode = courseCode;
		this.courseName = courseName;
		this.instructorName = instructorName;
		this.instructorScore = instructorScore;
	}
	
	public void addSession(CourseSession session) {
		sessionList.add(session);
	}
	
	public ArrayList<CourseSession> getList() {
		return sessionList;
	}
	
	public int getCourseCode() {
		return courseCode;
	}
	public String getCourseName() {
		return courseName;
	}
	public String getInstructorName() {
		return instructorName;
	}
	public double getInstructorScore() {
		return instructorScore;
	}
	
	public String toString() {
		if (sessionList.size() == 0) return courseCode+": Empty sessionList";
		else return courseCode+": "+sessionList.size()+" sessions";
	}
}