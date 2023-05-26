import java.util.*;
import java.io.*;

public class Main{
	// Files
	static BufferedReader setupFile, scheduleFile;
	static PrintWriter outputFile;
	
	// Parameters
	static Schedule userSchedule;
	static String[] courseList;
	
	public static void main(String[] args) throws IOException {
		try {
			setupFile = new BufferedReader(new FileReader("ava_setup.txt"));
			scheduleFile = new BufferedReader(new FileReader("schedule.txt"));
			outputFile = new PrintWriter(new FileWriter("output_data.txt"));
		} catch (Exception e) {
			notifyException();
		}
		
		initSetup();
		initSchedule();
		
		outputFile.println("---Setup Complete---");
		
		setupFile.close();
		scheduleFile.close();
		outputFile.close();
	}
	
	static void initSetup() {
		try {
			// init custom named days of the week
			int daysOfWeek = Integer.parseInt(setupFile.readLine());
			userSchedule = new Schedule(daysOfWeek);
			for (int i = 0; i < daysOfWeek; i++) {
				userSchedule.setDayOfWeek(i, setupFile.readLine());
			}
			
			// add daily sessions to each day
			int sessionsPerDay = Integer.parseInt(setupFile.readLine());
			String[] startTime = new String[sessionsPerDay], endTime = new String[sessionsPerDay];
			for (int i = 0; i < sessionsPerDay; i++) {
				StringTokenizer st = new StringTokenizer(setupFile.readLine(), " ");
				startTime[i] = st.nextToken(); endTime[i] = st.nextToken();
			}
			for (int i = 0; i < daysOfWeek; i++) {
				for (int j = 0; j < sessionsPerDay; j++) {
					userSchedule.addSession(i, startTime[j], endTime[j]);
				}
			}
			
			// init custom named course list
			int courseCnt = Integer.parseInt(setupFile.readLine());
			courseList = new String[courseCnt];
			for (int i = 0; i < courseCnt; i++) courseList[i] = setupFile.readLine();
		} catch (Exception e) {
			notifyException();
		}
	}
	
	static void initSchedule() {
		
	}
	
	static void notifyException() {
		println("Exception found, program halted");
		outputFile.println("Exception found, program halted");
		
		outputFile.close();
		System.exit(0);
	}
	
	static void print(Object obj) {System.out.print(obj);}
	static void println(Object obj) {System.out.println(obj);}
}