package com.ef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Log {
	public List<Tuple> loglist= null;
	public String fileName;

	public String duration;
	public int threshold;
	public String DateTime;

	Log(String fileName, String DateTime, String duration, int threshold) throws ParseException{
		this.fileName = fileName;
		this.loglist = new ArrayList<Tuple>();

		this.duration = duration;
		this.threshold = threshold;
		this.DateTime = DateTime;

		readLogFile();
	}

	public void readLogFile() throws ParseException{
		BufferedReader br = null;
		FileReader fr = null;

		try {
			br = new BufferedReader(new FileReader(new File(fileName)));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				//make line a tuple obj
				//add it to list
//				System.out.println(sCurrentLine);
				Tuple t = new Tuple(sCurrentLine);
				loglist.add(t);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	 public Stream<Tuple> getRecordsBtnDuration(String startDate,String duration){
		 return loglist.stream().filter(
						r->((convertDateToLocalTime(r.dateTime).isAfter(convertDateToLocalTime(startDate))
						&&convertDateToLocalTime(r.dateTime).isBefore(computeEndDate(startDate,duration))
						))
						);
	 }

	 private LocalDateTime convertDateToLocalTime(String date){
			return LocalDateTime.of(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")),
							LocalDateTime.now().toLocalTime()
							);
		}
	 private LocalDateTime convertDateToLocalTime(Date date){
			return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		}

	 private LocalDateTime computeEndDate(String startDate,String duration){
		 if(duration=="hourly")
			 return convertDateToLocalTime(startDate).plusHours(1);
		 else if(duration=="daily")
			return convertDateToLocalTime(startDate).plusDays(1);
		 return null;
	}

	 public HashMap<String,List<Tuple>> groupRecordsByIpAddress(String startDate,String duration, int threshold){
			return (HashMap<String, List<Tuple>>) groupRecordsByIpAddress(startDate,duration)
					.entrySet()
		            .stream()
		            .filter(a->a.getValue().size()>threshold)
		            .collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));
		}

	 public HashMap<String,List<Tuple>> groupRecordsByIpAddress(String startDate,String duration){
			return (HashMap<String, List<Tuple>>) getRecordsBtnDuration(startDate,duration)
					.collect(Collectors.groupingBy(Tuple::getIpAddress));
		}
}
