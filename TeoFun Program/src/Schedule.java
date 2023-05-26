import java.util.*;

public class Schedule {
	private HashMap<Integer, String> convDayIntStr = new HashMap<>();
	private HashMap<String, Integer> convDayStrInt = new HashMap<>();
	private HashMap<Integer, Integer> convSessIdx = new HashMap<>();
	private ArrayList<Session>[] scheduleLayout;
	private ArrayList<Integer>[] scheduleAva;// courseCode (-1:ava, couseCode:n/a)
	
	// Helper
	private int sessionIdxCnt = 0;
	
	public Schedule(int dayCnt) {
		scheduleLayout = new ArrayList[dayCnt];
		scheduleAva = new ArrayList[dayCnt];
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
		scheduleAva[day].add(-1);
//		System.out.println(convDayIntStr.get(day)+": "+startTime+" - "+endTime);
	}
	
	public ArrayList<Session>[] getScheduleLayout() {
		return scheduleLayout;
	}
	public ArrayList<Integer>[] getSchedule() {
		return scheduleAva;
	}
	
	private boolean setSessState(String dayStr, int startTime, int courseCode) {// courseCode (-1:ava, couseCode:n/a)
		int day = convDayStrInt.get(dayStr);
		int idx = convSessIdx.get(startTime);
		
		if (scheduleAva[day].get(idx) != -1 && courseCode != -1) return false;
		scheduleAva[day].set(idx, courseCode);
		return true;
	}
	
	public boolean setCourseState(Course course, boolean state) {// state (true:set false:remove)
		ArrayList<CourseSession> list = course.getList();
		for (int i = 0; i < list.size(); i++) {
			CourseSession curSess = list.get(i);
			if (state && !setSessState(curSess.getDayStr(), curSess.getStartTime(), course.getCourseCode())) {
				for (int j = i-1; j >= 0; j--) {
					CourseSession tmpSess = list.get(j);
					setSessState(tmpSess.getDayStr(), tmpSess.getStartTime(), -1);
				}
				return false;
			}
			if (!state) setSessState(curSess.getDayStr(), curSess.getStartTime(), -1);
		}
		return true;
	}
	
	public int getDayCnt() {
		return convDayIntStr.size();
	}
	public String getDayStr(int idx) {
		return convDayIntStr.get(idx);
	}
}