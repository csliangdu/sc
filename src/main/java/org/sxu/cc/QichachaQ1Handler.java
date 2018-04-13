package org.sxu.cc;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;



import io.parallec.core.ParallecResponseHandler;
import io.parallec.core.ResponseOnSingleTask;

public class QichachaQ1Handler implements ParallecResponseHandler {

	@SuppressWarnings("unchecked")
	public void onCompleted(ResponseOnSingleTask res, Map<String, Object> responseContext) {
		HashMap<String, String> failedPageMap = (HashMap<String, String>) responseContext.get("failedPageMap");//url-status
		HashMap<String, ArrayList<String>> pageCCListMap = (HashMap<String, ArrayList<String>>) responseContext.get("pageCCListMap");//i-json
		String path = res.getRequest().getResourcePath();
		if (res.getError()){
			failedPageMap.put(path, "ParallecError");
			return;
		} else if (res.getStatusCodeInt() != 200){
			failedPageMap.put(path, "StatusNot200");
			return;
		} else if (res.getStatusCodeInt() == 200){
			Document doc = Jsoup.parse(res.getResponseContent());
			ArrayList<String> ddList;
			try {
				ddList = CCDetail.getCurrentDailyDetailFromDoc(doc);
				if (ddList.size() > 0){
					pageCCListMap.put(path, ddList);
					failedPageMap.put(path, "OK");
				} else {
					failedPageMap.put(path, "NotEnoughData");
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}