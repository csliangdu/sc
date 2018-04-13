package org.sxu.cc;

import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ResponseOnSingleTask;

public class CCYearHandler implements ParallecResponseHandler {

	@SuppressWarnings("unchecked")
	public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {
		HashMap<String, Integer> ccYearNo = (HashMap<String, Integer>) responseContext.get("ccYearNo");//
		HashMap<String, String> failedPageMap = (HashMap<String, String>) responseContext.get("failedPageMap");//url-status
		String path = res.getRequest().getResourcePath();

		if (res.getError()){
			failedPageMap.put(path, "ParallecError");
			return;
		} else if (res.getStatusCodeInt() != 200){
			failedPageMap.put(path, "StatusNot200");
			return;
		} else if (res.getStatusCodeInt() == 200){
			Document doc = Jsoup.parse(res.getResponseContent());
			
			Elements e6 = doc.getElementsByClass("pull-left");
			if (e6.isEmpty()){
				failedPageMap.put(path, "data is Empty");
				return;
			}
			Elements links = e6.get(1).getElementsByTag("a");			
			for (int i=0; i < links.size(); i++){
				String x = links.get(i).text();
				if (x.contains("(") && x.contains(")")){
					String year = links.get(i).attr("data-value");
					String y = x.substring(x.indexOf("(")+1, x.indexOf(")"));
					int no = Integer.parseInt(y);
					ccYearNo.put(year, no);
					failedPageMap.put(path, "OK");
				} else {
					failedPageMap.put(path, "NotEnoughData");
				}
			}
		}
	}
}