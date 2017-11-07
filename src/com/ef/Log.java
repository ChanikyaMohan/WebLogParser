package com.ef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
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
import jdbc_connection.*;

public class Log {
	public List<Tuple> loglist= null;
	public String fileName;

//	public String duration;
//	public int threshold;
//	public String DateTime;

	Log(String fileName) throws ParseException{
		this.fileName = fileName;
		this.loglist = new ArrayList<Tuple>();

//		this.duration = duration;
//		this.threshold = threshold;
//		this.DateTime = DateTime;

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
						&&convertDateToLocalTime(r.dateTime).isBefore(upperLimitDate(startDate,duration))
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

	private LocalDateTime upperLimitDate(String startDate,String duration){
		 LocalDateTime d;
		 switch(duration){
		 	case "hourly":  d = convertDateToLocalTime(startDate).plusHours(1); break;
		 	case "daily": d = convertDateToLocalTime(startDate).plusDays(1); break;
		 	default: d = null;
		 }
		 return d;
	}

	 public HashMap<String,List<Tuple>> groupTuplesByIp(String startDate,String duration, int threshold){
			return (HashMap<String, List<Tuple>>) groupTuplesByIp(startDate,duration)
					.entrySet()
		            .stream()
		            .filter(a->a.getValue().size()>threshold)
		            .collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));
		}

	 public HashMap<String,List<Tuple>> groupTuplesByIp(String startDate,String duration){
			return (HashMap<String, List<Tuple>>) getRecordsBtnDuration(startDate,duration)
					.collect(Collectors.groupingBy(Tuple::getIpAddress));
		}

	private String getComment(Integer key) {
		// TODO Auto-generated method stub
		 String response="";

		 switch(key){
			 case 400: response= "400 Bad Request";break;
			 case 401: response= "401 Unauthorized";break;
			 case 402: response= "402 Payment Required";break;
			 case 403: response= "403 Forbidden";break;
			 case 404: response= "404 Not Found";break;
			 case 405: response= "405 Method Not Allowed";break;
			 case 406: response= "406 Not Acceptable";break;
			 case 407: response= "407 Proxy Authentication Required";
			 case 408: response= "408 Request Timeout";break;
			 case 409: response= "409 Conflict";break;
			 case 410: response= "410 Gone";break;
			 case 411: response= "411 Length Required";break;
			 case 412: response= "412 Precondition Failed";break;
			 case 413: response= "413 Request Entity Too Large";break;
			 case 414: response= "414 Request-URI Too Long";break;
			 case 415: response= "415 Unsupported Media Type";break;
			 case 416: response= "416 Requested Range Not Satisfiable";break;
			 case 417: response= "417 Expectation Failed";break;
			 case 418: response= "418 I'm a teapot (RFC 2324)";break;
			 case 420: response= "420 Enhance Your Calm (Twitter)";break;
			 case 422: response= "422 Unprocessable Entity (WebDAV)";break;
			 case 423: response= "423 Locked (WebDAV)";break;
			 case 424: response= "424 Failed Dependency (WebDAV)";break;
			 case 425: response= "425 Reserved for WebDAV";break;
			 case 426: response= "426 Upgrade Required";break;
			 case 428: response= "428 Precondition Required";break;
			 case 429: response= "429 Too Many Requests";break;
			 case 431: response= "431 Request Header Fields Too Large";break;
			 case 444: response= "444 No Response (Nginx)";break;
			 case 449: response= "449 Retry With (Microsoft)";break;
			 case 450: response= "450 Blocked by Windows Parental Controls (Microsoft)";break;
			 case 451: response= "451 Unavailable For Legal Reasons";break;
			 case 499: response= "499 Client Closed Request (Nginx)";
			 default: response= "200 Not Blocked";
		 }
		return response;
	}

	 public void ipAddressToComment(String startDate,String duration, int threshold){

		//database connection using JDBC
		MySQLConn sqlConn = new MySQLConn();
		System.out.println("Connection to database established successfully");
		//clear all tables of existing data
		try {
			sqlConn.clearAllTables();
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		List<OutputObject> comments = new ArrayList<OutputObject>();
		int ind = startDate.indexOf('.');
		char[] datearr = startDate.toCharArray();
		datearr[ind] = ' ';
		startDate = String.valueOf(datearr);
		startDate = startDate+".000";
		groupTuplesByIp(startDate,duration,threshold)
					.entrySet()
		            .stream().forEach(filteredTuple->{
        				filteredTuple.getValue().stream().distinct()
        				.collect(Collectors.groupingBy(Tuple::getResponseCode))
        				.entrySet().stream().forEach(groupedResponseTuple->{
        					//building a comment object with ipaddress,response comment & date
	        				OutputObject comment = new OutputObject(filteredTuple.getKey(),getComment(groupedResponseTuple.getKey()));

	        				//to print on console
	        				comments.add(comment);

        					//insert into database
	        				int ip_id;
	        				int comment_id;
	        				int log_id;
							try {
								ip_id = sqlConn.insertIntoIpaddresses(filteredTuple.getKey());
								comment_id = sqlConn.insertIntoComments(getComment(groupedResponseTuple.getKey()));
		        				log_id = sqlConn.insertIntoLogFiltered(ip_id,comment_id);

		        				//for each ip,comment pair enter their respective dates into log_dates table
		        				for(Tuple t: groupedResponseTuple.getValue()){
		        					sqlConn.insertIntoLogDates(log_id,convertDateToLocalTime(t.getDate()));
		        				}
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
	    				});
	        		});

		try {
			//close DB connection
			sqlConn.closeConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("  IP     -----     COMMENT  ");
		comments.stream().forEachOrdered(c->System.out.println(c.ipAdress+" : "+c.comment));

	}
}
