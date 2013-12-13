package com.hyc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FileOpen {
	private static ArrayList<String> logs = new ArrayList<String>();
	private static StringBuilder log = new StringBuilder();
	private static int i = 0;
	private static int lineNumber = 0;

	public static int getLineNumber() {
		return lineNumber;
	}

	public static int getI() {
		return i;
	}

	public static String getLog() {
		return log.toString();
	}

	public static ArrayList<String> readFile02(String filePath) throws IOException {
		log = new StringBuilder();
		i = 0;
		lineNumber = 0;
		FileInputStream fis = new FileInputStream(filePath);
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
			if (line.length() > 1) {
				lineNumber++;
			}
		}
		br.close();
		isr.close();
		fis.close();
		fis = new FileInputStream(filePath);
		isr = new InputStreamReader(fis, "UTF-8");
		br = new BufferedReader(isr);
		logs = new ArrayList<String>(lineNumber);
		while ((line = br.readLine()) != null) {
			if (line.length() > 1) {
				i++;
				log.append(line + '\n');
				logs.add(line + '\n');
			}
		}
		br.close();
		isr.close();
		fis.close();
		if (logs.size() == 0) {
			logs.add("没有可以显示的结果！");
		}
		return logs;
	}
}