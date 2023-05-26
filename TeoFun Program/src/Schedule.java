import java.util.*;

public class Schedule {
	private HashMap<Integer, String> convDayIntStr = new HashMap<>();
	private HashMap<String, Integer> convDayStrInt = new HashMap<>();
	private ArrayList<Session>[] daySchedule;
	
	public Schedule(int dayCnt) {
		daySchedule = new ArrayList[dayCnt];
		for (int i = 0; i < dayCnt; i++) daySchedule[i] = new ArrayList<>();
	}
	
	public void setDayOfWeek(int idx, String dayStr) {
		convDayIntStr.put(idx, dayStr);
		convDayStrInt.put(dayStr, idx);
	}
	
	public void addSession(int day, String startTimeStr, String endTimeStr) {
		int startTime = Time.toMins(startTimeStr), endTime = Time.toMins(endTimeStr);
		
		daySchedule[day].add(new Session(startTime, endTime));
//		System.out.println(convDayIntStr.get(day)+": "+startTime+" - "+endTime);
	}
	
	public int getDayCnt() {
		return convDayIntStr.size();
	}
	public String getDayStr(int idx) {
		return convDayIntStr.get(idx);
	}
}