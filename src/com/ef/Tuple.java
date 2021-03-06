package com.ef;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Tuple {
	private static final SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public Date dateTime;
	public String ipAddress;
	public String request;
	public int responseCode;
	public String payload;

	public String getIpAddress() {
		return ipAddress;
	}

	public int getResponseCode(){
		return responseCode;
	}

	public Date getDate(){
		return dateTime;
	}

	Tuple(String lineInLogFile) throws ParseException{
		HashMap<String,String> split = splitLineToValues(lineInLogFile);
		this.dateTime = dateFormat.parse(split.get("dateTime"));
//		System.out.println(this.dateTime);
		this.ipAddress = split.get("ipAddress");
		this.request = split.get("request");
		this.responseCode = Integer.parseInt(split.get("responseCode"));
		this.payload = split.get("payload");
	}

	private HashMap<String, String> splitLineToValues(String lineInLogFile) {
		// TODO Auto-generated method stub
//		System.out.println(lineInLogFile);
		String[] values = lineInLogFile.split("\\|");
//		System.out.println(values[2]);
		HashMap<String,String> res = new HashMap<String,String>();

		res.put("dateTime", values[0]);
		res.put("ipAddress", values[1]);
		res.put("request", values[2]);
		res.put("responseCode", values[3]);
		res.put("payload", values[4]);

		return res;
	}



}
