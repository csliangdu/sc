package org.sxu.cc;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import io.parallec.core.ParallecHeader;
import io.parallec.core.ParallelClient;
import io.parallec.core.ParallelTaskBuilder;
import io.parallec.core.RequestProtocol;

public class QichachaQ1Extractor {
	final static HashMap<String, String> failedPageMap = new HashMap<String, String>();//url-status
	final static HashMap<String, Object> responseContext = new HashMap<String, Object>();
	final static long startTime = System.currentTimeMillis();
	
	final static HashMap<String, ArrayList<String>> pageCCListMap = new HashMap<String, ArrayList<String>>();//url-data
	final static String host = "www.qichacha.com";
	final static HashMap<String, Integer> ccYearNo = new HashMap<String, Integer>();
	static String queryCN = "";
	static String cookieAio = "";
	final static String localDir = "ccdata";
	final static String destDir = System.getProperty("user.dir") + File.separator + localDir ;	
	public static void execute(String query, String cookie) throws InterruptedException{
		queryCN = query;
		cookieAio = cookie;
		if(FileHandler.mkdir(localDir) == 0){
			return;
		};
		 
		pageCCListMap.clear();
		String q2 = "";
		try {
			String q1 = URLEncoder.encode(query, "UTF-8");
			q2 = URLEncoder.encode(q1, "UTF-8");			
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (q2.isEmpty()){
			return;
		}
		ArrayList<String> queryUrl = new ArrayList<String>();
		queryUrl.add(q2 + "&ajaxflag=1&index=2");
		getYearNO(queryUrl);
		FileHandler.writeFile(destDir + File.separator + queryCN + "_Year_No.json", ccYearNo.toString());
		for (Entry<String, Integer> x: ccYearNo.entrySet()){
			String year = x.getKey();
			int n = x.getValue();
			oneQueryOneYear(q2, year, n);
		}		
		ccYearNo.clear();
	}

	public static void oneQueryOneYear(String query, String year, int nCC){
		pageCCListMap.clear();
		failedPageMap.clear();
		ArrayList<String> currencyURLs = new ArrayList<String>();
		for (int i=0; i < (nCC / 10) + 1; i++){
			String q3 = "/search_intellectualInfo?key=" + query + "&ajaxflag=1&index=2&groupYear=" + year + "&p=" + String.valueOf(i+1) + "&";
			currencyURLs.add(q3);
		}
		int minError = Integer.MAX_VALUE;
		boolean done = false;
		while (!done){
			ArrayList<String> currencyURLLeft = new ArrayList<String>();
			for (int i = 0; i < currencyURLs.size(); i++){
				String key = currencyURLs.get(i);
				String status = failedPageMap.get(key);
				if (!failedPageMap.containsKey(key) || status.isEmpty() || !("OK".equals(status) || "PageParseError".equals(status) || "OtherError".equals(status)) ){
					currencyURLLeft.add(currencyURLs.get(i));
				}
			}
			if (currencyURLLeft.isEmpty()){
				done = true;
			}
			if (currencyURLLeft.size() < minError){
				minError = currencyURLLeft.size();
			} else if (currencyURLLeft.size() == minError){
				done = true;
			}
			oneBatch(currencyURLLeft);		
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String errorInfo = failedToString();
		if (!errorInfo.isEmpty()){
			FileHandler.writeFile(destDir + File.separator + queryCN + "_" + year + "_top_" + nCC + "_failed.json", errorInfo);
		}
		FileHandler.writeFile(destDir + File.separator + queryCN + "_" + year + "_top_" + nCC + ".json", successToString());
		pageCCListMap.clear();
		failedPageMap.clear();
	}

	public static void oneBatch(ArrayList<String> currencyURLsLeft){
		ParallelClient pc = new ParallelClient();		
		ParallecHeader pheader = new ParallecHeader();
		pheader.addPair("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		pheader.addPair("Accept-Encoding", "gzip, deflate");
		pheader.addPair("Accept-Language", "zh-CN,zh;q=0.8");
		pheader.addPair("Cache-Control", "max-age=0");
		pheader.addPair("Connection", "keep-alive");
		pheader.addPair("Cookie", cookieAio);
		pheader.addPair("Host", "www.qichacha.com");
		pheader.addPair("Referer", "http://www.qichacha.com/search_intellectualinfo?key=%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8");
		pheader.addPair("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		pheader.addPair("X-Requested-With", "XMLHttpRequest");
		pheader.addPair("Upgrade-Insecure-Requests", "1");
		ParallelTaskBuilder ptb = pc.prepareHttpGet("$KEY")
				.setProtocol(RequestProtocol.HTTP)
				.setHttpHeaders(pheader)
				.setConcurrency(5)
				.setHttpPort(80)
				.setReplaceVarMapToSingleTargetSingleVar("KEY", currencyURLsLeft, host)
				.setResponseContext(responseContext);

		responseContext.put("startTime", startTime);
		responseContext.put("destDir", destDir);
		responseContext.put("failedPageMap", failedPageMap);
		responseContext.put("pageCCListMap", pageCCListMap);

		QichachaQ1Handler cmHandler = new QichachaQ1Handler();
		ptb.execute(cmHandler);
	}

	public static void getYearNO(ArrayList<String> queryUrl){
		ParallelClient pc = new ParallelClient();		
		ParallecHeader pheader = new ParallecHeader();
		pheader.addPair("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
		pheader.addPair("Accept-Encoding", "gzip, deflate");
		pheader.addPair("Accept-Language", "zh-CN,zh;q=0.8");
		pheader.addPair("Cache-Control", "max-age=0");
		pheader.addPair("Connection", "keep-alive");
		pheader.addPair("Cookie", cookieAio);
		pheader.addPair("Host", "www.qichacha.com");
		pheader.addPair("Referer", "http://www.qichacha.com/search_intellectualinfo?key=%E7%A7%91%E6%8A%80%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8");
		pheader.addPair("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36");
		pheader.addPair("X-Requested-With", "XMLHttpRequest");
		pheader.addPair("Upgrade-Insecure-Requests", "1");
		ParallelTaskBuilder ptb = pc.prepareHttpGet("/search_intellectualInfo?key=$KEY")
				.setProtocol(RequestProtocol.HTTP)
				.setHttpHeaders(pheader)
				.setConcurrency(10)
				.setReplaceVarMapToSingleTargetSingleVar("KEY", queryUrl, host)
				.setResponseContext(responseContext);

		responseContext.put("failedPageMap", failedPageMap);
		responseContext.put("ccYearNo", ccYearNo);

		CCYearHandler cmHandler = new CCYearHandler();
		ptb.execute(cmHandler);
	}

	public static String failedToString(){
		StringBuffer sb = new StringBuffer();
		boolean flag = false;
		for (Entry<String, String> entry : failedPageMap.entrySet()){
			String key = entry.getKey();
			String val = entry.getValue();
			if (!"OK".equals(val)){
				flag = true;
				sb.append("{");
				sb.append("\"query\":\"" + queryCN + "\",");
				sb.append("\"page\":\"" + key.substring(key.indexOf("&p=") + 3, key.lastIndexOf("&")) + "\",");
				sb.append("\"year\":\"" + key.substring(key.indexOf("groupYear=") + 10, key.indexOf("&p=")) + "\"},");
			}
		}
		String sb_tmp = sb.toString();
		if (!sb_tmp.isEmpty()){
			sb_tmp = sb_tmp.substring(0, sb_tmp.lastIndexOf(","));
		}
		if (flag){
			sb_tmp = "[" + sb_tmp + "]";
			sb_tmp = JsonFormatTool.formatJson(sb_tmp);
			return sb_tmp;
		} else {
			return "";
		}
	}

	public static String successToString(){
		StringBuffer sb = new StringBuffer();
		for (Entry<String, ArrayList<String>> entry : pageCCListMap.entrySet()){
			ArrayList<String> val = entry.getValue();
			for (String x: val){
				sb.append(x + ",");
			}
		}
		String sb_tmp = sb.toString();
		if (!sb_tmp.isEmpty()){
			sb_tmp = sb_tmp.substring(0, sb_tmp.lastIndexOf(","));
		}
		sb_tmp = "[" + sb_tmp + "]";
		sb_tmp = JsonFormatTool.formatJson(sb_tmp);
		return sb_tmp;
	}
}
