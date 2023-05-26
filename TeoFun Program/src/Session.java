public class Session {
	private int startTime, endTime;
	
	public Session(int startTime, int endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public int getStartTime() {
		return startTime;
	}
	public int getEndTime() {
		return endTime;
	}
}