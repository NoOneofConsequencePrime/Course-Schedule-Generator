import java.util.*;

public class Course {
	private int courseCode;
	private String courseName, instructorName;
	private ArrayList<CourseSession> sessionList = new ArrayList<>();
	
	public Course(int courseCode, String courseName, String instructorName) {
		this.courseCode = courseCode;
		this.courseName = courseName;
		this.instructorName = instructorName;
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
	
	public String toString() {
		if (sessionList.size() == 0) return "Empty sessionList";
		else return courseCode+": "+sessionList.size()+" sessions";
	}
}