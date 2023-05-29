/*
 * Project Name: NYU Class Schedule Generator
 * Made by: Bryan Zhao
 * Date: May 25th 2023
 * Version: v1.1.0
 */

import java.util.*;
import java.io.*;
import java.text.*;

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
	static HashMap<String, Double> instructorRating = new HashMap<>();
	static ArrayList<OutputFormat> ansList = new ArrayList<>();
	
	static NumberFormat nf;
	
	public static void main(String[] args) throws IOException {
		try {
			setupFile = new BufferedReader(new FileReader("ava_setup.txt"));
			scheduleFile = new BufferedReader(new FileReader("schedule.txt"));
			outputFile = new PrintWriter(new FileWriter("output_data.txt"));
		} catch (Exception e) {
			notifyException("main - file lookup");
		}
		
		initProgram();
		initSetup();
		println("---Setup Complete (1/2)---");
		initSchedule();
		println("---Setup Complete (2/2)---");
		
		genPerms(0);
		postProcess();
		printAns();
		
		setupFile.close();
		scheduleFile.close();
		outputFile.close();
	}
	
	static void initProgram() {
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
	}
	
	static void initSetup() {
		try {
			// init custom named course list
			int courseCnt = Integer.parseInt(setupFile.readLine());
			courseList = new ArrayList[courseCnt];
			for (int i = 0; i < courseCnt; i++) {
				courseList[i] = new ArrayList<>();
			}
			
			// init custom named days of the week
			int daysOfWeek = Integer.parseInt(setupFile.readLine());
			userSchedule = new Schedule(daysOfWeek, courseCnt, MOD, modBase);
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
			
			// init instructorRating
			int instructorCnt = Integer.parseInt(setupFile.readLine());
			for (int i = 0; i < instructorCnt; i++) {
				String instructorName = setupFile.readLine();
				double instructorScore = Double.parseDouble(setupFile.readLine());
				instructorRating.put(instructorName, instructorScore);
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
					
					Course tmpCourse = new Course(courseCode, courseName, instructorName, instructorRating.get(instructorName));

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
		try {
			if (courseIdx == courseList.length) {
				String tmpText = "";
				tmpText += "ID: "+userSchedule.getScheduleID();
				HashMap<Integer, String> convDayIntStr = userSchedule.getConvDayIntStr();
				for (int i = 0; i < convDayIntStr.size(); i++) tmpText += ","+convDayIntStr.get(i);
				tmpText += ",,Course,Instructor,"+nf.format(userSchedule.getInstructorRating())+"\n";
				
				ArrayList<Session>[] scheduleLayout = userSchedule.getScheduleLayout();
				ArrayList<Course>[] schedule = userSchedule.getSchedule();
				int courseDisplayIdx = 0;
				for (int i = 0; i < scheduleLayout[0].size(); i++) {// session times
					Session curSess = scheduleLayout[0].get(i);
					tmpText += Time.toText(curSess.getStartTime())+" - "+Time.toText(curSess.getEndTime());
					for (int j = 0; j < schedule.length; j++) {// session days
						Course curCourse = schedule[j].get(i);
						tmpText += ",";
//						if (courseCode != -1) outputFile.print(courseCode+" ("+courseLookup.get(courseCode).getCourseName()+")");
						if (curCourse != null) tmpText += curCourse.getCourseCode();
					}
					
					Course[] courseList = userSchedule.getCourseList();
					if (courseDisplayIdx < courseList.length) {
						Course curCourse = courseList[courseDisplayIdx];
						tmpText += ",,";
						tmpText += curCourse.getCourseName()+" ("+curCourse.getCourseCode()+"),"+curCourse.getInstructorName();
						courseDisplayIdx++;
					}
					tmpText += "\n";
				}
				tmpText += "\n\n";
				
				OutputFormat tmpOutput = new OutputFormat(tmpText, userSchedule.getInstructorRating());
				ansList.add(tmpOutput);
				
				println(userSchedule);
				
//				println("-----");
//				for (int i = 0; i < courseList.length; i++) {
//					println(courseList[i]);
//				}
				
				return;
			}
		} catch (Exception e) {
			notifyException("genPerms()");
		}
		
		for (int i = 0; i < courseList[courseIdx].size(); i++) {
			Course curCourse = courseList[courseIdx].get(i);
			
			// set cur course as option
			if (!userSchedule.setCourseState(curCourse, courseIdx, true)) {
				continue;
			}
			
			// run recur
			genPerms(courseIdx+1);
			
			// remove cur course as option
			userSchedule.setCourseState(curCourse, courseIdx, false);
		}
	}
	
	static void postProcess() {
		Collections.sort(ansList);
	}
	
	static void printAns() {
		outputFile.println("Possibilities generated: "+ansList.size()+"\n");
		for (OutputFormat curOutput : ansList) {
			outputFile.print(curOutput.getOutputText());
		}
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