package org.sxu.cc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class QichachaCCLoader {
	public static void main(String[] args){
		if (args.length == 0){
			System.out.println("------------------------QichachaCCLoader loading SoftFrom Qichacha---------------------");
			System.out.println("------------------    loading Software Copyright Info From Qichacha   ------------------");
			System.out.println("Usage:");
			System.out.println("    java -jar x.jar queryFilepath cookieFilePath ");
			System.out.println("");
			System.out.println("");
		}
		String filePath = args[0];
		String cookiePath = "";
		if (args.length == 2){
			cookiePath = args[1];
		}
		
		InputStream is;
		InputStream is2;
		try {
			is = new FileInputStream(filePath);
			ArrayList<String> queryList = FileHandler.getArrayListFromFile(is);
			
			is2 = new FileInputStream(cookiePath);
			String cookie = FileHandler.getStringFromFile(is2);
			
			
			for (String query : queryList){
				try {
					QichachaQ1Extractor.execute(query, cookie);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		};

	}
}
