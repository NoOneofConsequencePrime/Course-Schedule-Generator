import java.util.*;

public class Time {
	public static int toMins(String str) {
		StringTokenizer st = new StringTokenizer(str, ":");
		int ret = Integer.parseInt(st.nextToken())*60+Integer.parseInt(st.nextToken());
		
		return ret;
	}
	
	public static String toText(int mins) {
		String ret = mins/60+":";
		if (mins%60 < 10) ret += "0";
		ret += mins%60;
		
		return ret;
	}
}