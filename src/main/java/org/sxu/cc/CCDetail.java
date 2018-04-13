package org.sxu.cc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class CCDetail {
	public String ccName = "";
	public String ccNo = "";
	public String ccShortName = "";
	public String ccOwner = "";

	public String toString(){
		String s1 = "{\"ccName\":\"" + ccName + 
				"\",\"ccNo\":\"" + ccNo +
				"\",\"ccShortName\":\"" + ccShortName + 
				"\",\"ccOwner\":\"" + ccOwner +
				"\"}";
		return s1;
	}

	public static ArrayList<String> getCurrentDailyDetailFromDoc(Document doc) throws UnsupportedEncodingException{
		ArrayList<String> ddList = new ArrayList<String>();
		Elements e6 = doc.select("#searchlist > table > tbody");
		if (e6.isEmpty()){
			return ddList;
		}
		Element tbody = e6.get(0);
		Elements trs = tbody.getElementsByTag("tr");
		String s1 = "登记号：";
		s1 = new String(s1.getBytes(), "utf-8");
		String s2 = "称：";
		s2 = new String(s2.getBytes(), "utf-8");
		for (int i=0; i < trs.size(); i++){
			Elements e7 = trs.get(i).getElementsByTag("td");
			
			Element e8 = e7.get(0);
			System.out.println("TBODY=");
			System.out.println("      " + tbody.toString());
			CCDetail cd = new CCDetail();
			cd.ccName = e8.getElementsByAttributeValue("class", "ma_h1").text();
			String s3 = e8.text();
			
			cd.ccNo = s3.split(s1)[1].split(" ")[0];
			cd.ccShortName = s3.split(s2)[1].split(" ")[0];
			cd.ccOwner = e8.getElementsByAttributeValue("class", "c_a").text();
			ddList.add(cd.toString());			
		}
		return ddList;
	}
}

