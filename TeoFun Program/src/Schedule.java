import java.util.*;
import java.io.*;
import java.text.*;

public class Schedule {
	// Global Settings
	static private long MOD;
	static private int[] modBase;
	
	// Variables
	private int dayCnt, courseCnt;
	
	// Objects
	private HashMap<Integer, String> convDayIntStr = new HashMap<>();
	private HashMap<String, Integer> convDayStrInt = new HashMap<>();
	private HashMap<Integer, Integer> convSessIdx = new HashMap<>();
	private ArrayList<Session>[] scheduleLayout;
	private ArrayList<Course>[] scheduleAva;// course (null:ava, "Course":n/a)
	private Course[] courseList;// course (null:ava, "Course":n/a)
	private long scheduleID;
	private double instructorRating;
	
	// Helper Var
	private int sessionIdxCnt = 0;
	
	public Schedule(int dayCnt, int courseCnt, long MOD, int[] modBase) {
		this.dayCnt = dayCnt;
		this.courseCnt = courseCnt;
		this.MOD = MOD;
		this.modBase = modBase;
		scheduleLayout = new ArrayList[dayCnt];
		scheduleAva = new ArrayList[dayCnt];
		courseList = new Course[courseCnt];
		for (int i = 0; i < dayCnt; i++) scheduleLayout[i] = new ArrayList<>();
		for (int i = 0; i < dayCnt; i++) scheduleAva[i] = new ArrayList<>();
	}
	
	public void setDayOfWeek(int idx, String dayStr) {
		convDayIntStr.put(idx, dayStr);
		convDayStrInt.put(dayStr, idx);
	}
	
	public HashMap<Integer, String> getConvDayIntStr() {
		return convDayIntStr;
	}
	
	public void addSession(int day, String startTimeStr, String endTimeStr) {
		int startTime = Time.toMins(startTimeStr), endTime = Time.toMins(endTimeStr);

		if (!convSessIdx.containsKey(startTime)) {
			convSessIdx.put(startTime, sessionIdxCnt);
			sessionIdxCnt++;
//			System.out.println(startTime+": "+convSessIdx.get(startTime));
		}
		scheduleLayout[day].add(new Session(startTime, endTime));
		scheduleAva[day].add(null);
//		System.out.println(convDayIntStr.get(day)+": "+startTime+" - "+endTime);
	}
	
	public ArrayList<Session>[] getScheduleLayout() {
		return scheduleLayout;
	}
	public ArrayList<Course>[] getSchedule() {
		return scheduleAva;
	}
	
	private boolean setSessState(String dayStr, int startTime, Course course) {// courseCode (-1:ava, couseCode:n/a)
		int day = convDayStrInt.get(dayStr);
		int idx = convSessIdx.get(startTime);
		
		if (scheduleAva[day].get(idx) != null && course != null) return false;
		scheduleAva[day].set(idx, course);
		return true;
	}
	
	public boolean setCourseState(Course course, int courseIdx, boolean state) {// state (true:set false:remove)
		ArrayList<CourseSession> list = course.getList();
		for (int i = 0; i < list.size(); i++) {
			CourseSession curSess = list.get(i);
			if (state && !setSessState(curSess.getDayStr(), curSess.getStartTime(), course)) {
				for (int j = i-1; j >= 0; j--) {
					CourseSession tmpSess = list.get(j);
					setSessState(tmpSess.getDayStr(), tmpSess.getStartTime(), null);
				}
				return false;
			}
			if (!state) {
				setSessState(curSess.getDayStr(), curSess.getStartTime(), null);
				courseList[courseIdx] = null;
			}
		}
		courseList[courseIdx] = course;
		if (courseIdx+1 == courseCnt) finalizeSchedule();
		return true;
	}
	
	public int getDayCnt() {
		return convDayIntStr.size();
	}
	public String getDayStr(int idx) {
		return convDayIntStr.get(idx);
	}
	public long getScheduleID() {
		return scheduleID;
	}
	public Course[] getCourseList() {
		return courseList;
	}
	public double getInstructorRating() {
		return instructorRating;
	}
	
	private void finalizeSchedule() {
		scheduleID = calcScheduleID();
		instructorRating = 0;
		int instructorCnt = 0;
		for (Course curCourse : courseList) {
			if (curCourse.getInstructorScore() != -1) {
				instructorRating += curCourse.getInstructorScore();
				instructorCnt++;
			}
		}
		instructorRating /= instructorCnt;
	}
	
	private long calcScheduleID() {
		long ret = 0;
		for (int i = 0; i < courseList.length; i++) {
			Course curCourse = courseList[i];
			ret += modPow(modBase[i], curCourse.getCourseCode(), MOD);
		}
		
		return ret;
	}
	private long modPow(long b, long e, long m) {
		long ret = 1;
		while (e > 0) {
			if ((e&1) == 1) ret = (ret*b)%m;
			e >>= 1;
			b = (b*b)%m;
		}
		return ret;
	}
	
	public String toString() {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
		String ret = "";
		ret += scheduleID+" ("+nf.format(instructorRating)+") - [";
		for (int i = 0; i < courseList.length; i++) {
			Course curCourse = courseList[i];
			ret += curCourse;
			if (i != courseList.length-1) ret += ", ";
		}
		ret += "]";
		
		return ret;
	}
}