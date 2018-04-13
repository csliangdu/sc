package org.sxu.cc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileHandler {
	private static final Logger logger = LogManager.getLogger(FileHandler.class);

	public static int mkdir(String destDir){
		File dir = new File(destDir);
		if (!dir.exists() || !dir.isDirectory()) {
			dir.mkdirs();
			if (!dir.exists() || !dir.isDirectory()){
				logger.error("Create Directory error");
				return 0;
			}
		}
		return 1;
	}

	public static void writeFile(String filePath, String fileContent) {
		try {
			FileWriter fw = new FileWriter(filePath);  
			PrintWriter out = new PrintWriter(fw);  
			out.write(fileContent); 
			out.println();  
			fw.close();  
			out.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static ArrayList<String> getArrayListFromFile(InputStream is){
		ArrayList<String> exnames = new ArrayList<String>();
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String y ="";
			try {
				while((y = br.readLine())!=null){
					exnames.add(y);
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		return exnames;
	}
	
	
	public static String getStringFromFile(InputStream is){
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String y ="";
		try {
			while((y = br.readLine())!=null){
				sb.append(y);
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
}
