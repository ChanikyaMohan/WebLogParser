package com.ef;

import java.text.ParseException;
import java.util.HashMap;

public class Parser {

	public static void main(String args[]) throws ParseException{


//		System.out.println(logfile.loglist.get(1));
		HashMap<String,String> arguments = new HashMap<String,String>();

		//getting all arguments and storing them in hashmap for quick access
		for(int i=0;i<args.length;i++){
			String[] split = args[i].substring(2).split("=");
			arguments.put(split[0], split[1]);
//			System.out.println(split[0]);
//			System.out.println(split[1]);
//			System.out.println(arguments.get(split));
		}

		if(arguments.isEmpty()){
			System.out.println("Arguments missing, please try again with proper arguments");
		} else {
			String startDate = arguments.get("startDate");
			String duration = arguments.get("duration");
			int threshold = Integer.parseInt(arguments.get("threshold"));

			//read log file and build an object
			Log logfile = new Log("C:\\Users\\konya\\workspace\\WebServerLogParser\\access.log",startDate,duration,threshold);

			System.out.println(logfile.getRecordsBtnDuration(startDate, duration));
		}

	}
}
