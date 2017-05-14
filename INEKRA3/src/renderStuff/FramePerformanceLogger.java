package renderStuff;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import collectionsStuff.ArrayListL;
import gameStuff.Err;
import toolBox.Tools;

public class FramePerformanceLogger {
	
	public static final boolean log = true;
	private static final Map<String, ArrayListL> map = new HashMap<String, ArrayListL>();
	
	private static float criticalFps = 50;
	
	public static void update(){
		float fps = DisplayManager.getFps();
		if(fps < criticalFps){
			printPerformanceLog(fps);
		}
		map.clear();
	}
	
	private static long lm;
	
	public static void stopTime(){
		lm = System.currentTimeMillis();
	}
	
	public static void writeStoppedTime(String keyword) {
		writeTime(keyword, System.currentTimeMillis()-lm);
		lm = System.currentTimeMillis();
	}
	
	public static void writeTime(String method, long millis){
		if(!log)return;
		ArrayListL longs = map.get(method);
		if(longs == null){
			longs = new ArrayListL();
			map.put(method, longs);
		}
		longs.add(millis);
	}
	
	private static boolean already = false;
	
	private static void printPerformanceLog(float fps){
		StringBuilder log = new StringBuilder();
		// if not already printed!
		if(!already){
			log.append("************************ FIRST LOG SINCE PROGRAM START************************\n");
			log.append("All Logs here printed in ");
			log.append(DisplayManager.getSystemTime(false));
			log.append('\n');
			already = true;
		}
		log.append(" ************ fps were at " + fps + " ************\n");
		for(String s : map.keySet()){
			log.append(s);
			log.append(' ');
			log.append(map.get(s).toString());
			long sum = map.get(s).sum();
			log.append(", together ");
			log.append(sum);
			log.append(", Ø ");
			log.append(sum/(double)map.get(s).size());
			log.append('\n');
		}
		log.append(" ************************\n");
		try {
			out.write(log.toString());
		} catch (IOException e) {
			e.printStackTrace(Err.err);
			Err.err.println("Wanted to print performancelog to logfile. Failed somehow! Here comes the full performancelog!");
			Err.err.println(log.toString());
		}
	}
	
	private static BufferedWriter out;
	static{
		try {
			out = Tools.getBufferedFileWriter("performanceLog.txt", false);
		} catch (IOException e) {
			e.printStackTrace(Err.err);
		}
	}
	
	public static void cleanUp(){
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace(Err.err);
		}
	}

}
