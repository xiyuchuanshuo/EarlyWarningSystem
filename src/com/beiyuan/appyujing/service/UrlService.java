package com.beiyuan.appyujing.service;

import java.net.URL;
import java.util.List;
import org.json.JSONObject;

public interface UrlService {

	JSONObject sentParams2Server(JSONObject obj);

	JSONObject sent2MapServer(JSONObject obj);

	JSONObject sentParams2DataServer(JSONObject obj, String name);

	JSONObject sentParams2Complete(JSONObject obj);

	String getJsonContent(String name);

	List<String> getCollegeList(String key, String jsonString);

	String sentParams2News(JSONObject obj);

	/*
 * 
 * 
 * 
	*/
	JSONObject linkServer(JSONObject obj, String URL);
}
