/*
 * Project Name: NYU Class Schedule Generator
 * Made by: Bryan Zhao
 * Date: May 25th 2023
 * Version: v1.0.5
 */

import java.util.*;
import java.io.*;

public class Main{
	// Files
	static BufferedReader setupFile, scheduleFile;
	static PrintWriter outputFile;
	
	// Settings
	static final long MOD = (long)878953;
	static final int[] modBase = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29};
	
	// Variables & Objects
	static Schedule userSchedule;
	static HashMap<Integer, Course> courseLookup = new HashMap<>();// courseCode -> courseName
	static ArrayList<Course>[] courseList;
	static Course[] userCourseList;
	static int possibilityCnt = 0;
	
	public static void main(String[] args) throws IOException {
		try {
			setupFile = new BufferedReader(new FileReader("ava_setup.txt"));
			scheduleFile = new BufferedReader(new FileReader("schedule.txt"));
			outputFile = new PrintWriter(new FileWriter("output_data.txt"));
		} catch (Exception e) {
			notifyException("main - file lookup");
		}
		
		initSetup();
		println("---Setup Complete (1/2)---");
		initSchedule();
		println("---Setup Complete (2/2)---");
		
		genPerms(0);
		outputFile.print("Possibilities generated: "+possibilityCnt);
		
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
			courseList = new ArrayList[courseCnt];
			userCourseList = new Course[courseCnt];
			for (int i = 0; i < courseCnt; i++) {
				courseList[i] = new ArrayList<>();
			}
		} catch (Exception e) {
			notifyException("initSetup()");
		}
	}
	
	static void initSchedule() {
		try {
			for (int idx = 0; idx < courseList.length; idx++) {// A B C D
				StringTokenizer st = new StringTokenizer(scheduleFile.readLine(), " ");
				String courseName = st.nextToken();
				int courseCnt = Integer.parseInt(st.nextToken());
				
				for (int i = 0; i < courseCnt; i++) {// 1 2 3
					st = new StringTokenizer(scheduleFile.readLine(), " ");
					int courseCode = Integer.parseInt(st.nextToken());
					String instructorName = st.nextToken();
					
					Course tmpCourse = new Course(courseCode, courseName, instructorName);
					
					st = new StringTokenizer(scheduleFile.readLine(), " ");
					String startTimeStr = st.nextToken(), endTimeStr = st.nextToken();
					
					int dayCnt = Integer.parseInt(scheduleFile.readLine());
					st = new StringTokenizer(scheduleFile.readLine(), " ");
					for (int j = 0; j < dayCnt; j++) {
						String dayStr = st.nextToken();
						tmpCourse.addSession(new CourseSession(dayStr, startTimeStr, endTimeStr));
					}
					
					courseList[idx].add(tmpCourse);
					courseLookup.put(courseCode, tmpCourse);
				}
			}
		} catch (Exception e) {
			notifyException("initSchedule()");
		}
	}
	
	static void genPerms(int courseIdx) {
		if (courseIdx == courseList.length) {
			outputFile.print("ID: "+getScheduleID());
			HashMap<Integer, String> convDayIntStr = userSchedule.getConvDayIntStr();
			for (int i = 0; i < convDayIntStr.size(); i++) outputFile.print(","+convDayIntStr.get(i));
			outputFile.print(",,Course,Instructor");
			outputFile.println("");
			
			ArrayList<Session>[] scheduleLayout = userSchedule.getScheduleLayout();
			ArrayList<Integer>[] schedule = userSchedule.getSchedule();
			int courseDisplayIdx = 0;
			for (int i = 0; i < scheduleLayout[0].size(); i++) {// session times
				Session curSess = scheduleLayout[0].get(i);
				outputFile.print(Time.toText(curSess.getStartTime())+" - "+Time.toText(curSess.getEndTime()));
				for (int j = 0; j < schedule.length; j++) {// session days
					int courseCode = schedule[j].get(i);
					outputFile.print(",");
//					if (courseCode != -1) outputFile.print(courseCode+" ("+courseLookup.get(courseCode).getCourseName()+")");
					if (courseCode != -1) outputFile.print(courseCode);
				}
				if (courseDisplayIdx < userCourseList.length) {
					Course curCourse = userCourseList[courseDisplayIdx];
					outputFile.print(",,");
					outputFile.print(curCourse.getCourseName()+" ("+curCourse.getCourseCode()+"),"+curCourse.getInstructorName());
					courseDisplayIdx++;
				}
				outputFile.println("");
			}
			
			outputFile.println("\n\n");
			
			println("-----");
			for (Course curCourse : userCourseList) {
				println(curCourse);
			}
			
			possibilityCnt++;
			return;
		}
		
		for (Course curCourse : courseList[courseIdx]) {
			// set cur course as option
			if (!userSchedule.setCourseState(curCourse, true)) {
				continue;
			}
			
			// update list + run recur
			userCourseList[courseIdx] = curCourse;
			genPerms(courseIdx+1);
			
			// remove cur course as option
			userSchedule.setCourseState(curCourse, false);
			userCourseList[courseIdx] = null;
		}
	}
	
	static long getScheduleID() {
		long ret = 0;
		for (int i = 0; i < userCourseList.length; i++) {
			Course curCourse = userCourseList[i];
			ret += modPow(modBase[i], curCourse.getCourseCode(), MOD);
		}
		
		return ret;
	}
	static long modPow(long b, long e, long m) {
		long ret = 1;
		while (e > 0) {
			if ((e&1) == 1) ret = (ret*b)%m;
			e >>= 1;
			b = (b*b)%m;
		}
		return ret;
	}
	
	static void notifyException(String str) {
		println("Exception found at "+str+" , program halted");
		outputFile.println("Exception found at "+str+" , program halted");
		
		outputFile.close();
		System.exit(0);
	}
	
	static void print(Object obj) {System.out.print(obj);}
	static void println(Object obj) {System.out.println(obj);}
}