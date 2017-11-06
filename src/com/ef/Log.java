package com.ef;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Log {
	public List<Tuple> loglist= null;
	public String fileName;
	Log(String fileName) throws ParseException{
		this.fileName = fileName;
		this.loglist = new ArrayList<Tuple>();
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
}
