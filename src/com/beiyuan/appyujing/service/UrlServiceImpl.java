package com.beiyuan.appyujing.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.util.Log;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.tools.HttpUtils;

public class UrlServiceImpl implements UrlService {
	private static final String TAG = "UrlServiceImpl";

	String IP = ConstantData.IP;

	/*
	 * 方法的优化参数封装多少参数都能用
	 */
	@Override
	public JSONObject sentParams2Server(JSONObject obj) {

		String uriAPI = "http://" + IP + "/school5/importData.html";
		Log.i("JSON", obj.toString());
		HttpEntity entity = HttpUtils.getHttpEntity(uriAPI, 2, obj);
		JSONObject jo2 = new JSONObject();
		Log.i("JSON", jo2.toString());
		InputStream in = HttpUtils.getInputStream(entity);
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				jo2 = new JSONObject(sb.toString());
				Log.i("JSON", jo2.toString());
				return jo2;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i("JSON", jo2.toString());
		return jo2;

	}

	/*
	 * 用于发送json数据
	 */
	@Override
	public JSONObject sentParams2DataServer(JSONObject obj, String url) {

		Log.i("JSON", "传过来的obj="+obj.toString());
		HttpEntity entity = HttpUtils.getHttpEntity(url, 2, obj);
		JSONObject jo2 = new JSONObject();
		InputStream in = HttpUtils.getInputStream(entity);
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				Log.i("test", sb.toString());
				jo2 = new JSONObject(sb.toString());
				Log.i("JSON", "返回的json  jo2="+jo2.toString());
				return jo2;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i("JSON", jo2.toString());
		return jo2;
	}

	@Override
	public JSONObject sentParams2Complete(JSONObject obj) {
		System.out.println("用于完善信息");
		String uriAPI = "http://" + IP + "/school5/userChange.html";

		// String strResult = "FAIL";

		Log.i("JSON", obj.toString());
		HttpEntity entity = HttpUtils.getHttpEntity(uriAPI, 2, obj);
		JSONObject jo2 = new JSONObject();
		Log.i("JSON", jo2.toString());
		InputStream in = HttpUtils.getInputStream(entity);
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				jo2 = new JSONObject(sb.toString());
				Log.i("JSON", jo2.toString());
				return jo2;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i("JSON", jo2.toString());
		return jo2;
	}

	/*
	 * 方法的优化参数封装多少参数都能用
	 */
	@Override
	public String sentParams2News(JSONObject obj) {

		String uriAPI = "http://" + IP + "/school5/newsList/client.html";
		// String uriAPI = "http://" + IP + "/JWGL_Server_1/LoginServlet";
		// String strResult = "FAIL";

		Log.i("JSON", obj.toString());
		HttpEntity entity = HttpUtils.getHttpEntity(uriAPI, 2, obj);
		String str = "FAIL";
		InputStream in = HttpUtils.getInputStream(entity);
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				str = sb.toString();
				return str;

			} catch (Exception e) {
				e.printStackTrace();
				return str;
			}
		}
		return str;

	}

	/**
	 * 获取学院、年级 数据
	 */
	@Override
	public ArrayList<String> getCollegeList(String key, String jsonString) {
		// TODO Auto-generated method stub
		ArrayList<String> listString = new ArrayList<String>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			// 返回JSON的数组
			JSONArray jsonArray = jsonObject.getJSONArray(key);
			for (int i = 0; i < jsonArray.length(); i++) {
				String msg = jsonArray.getString(i);
				listString.add(msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return listString;
	}

	public String getJsonContent(String urlname) {

		// String uriAPI = "http://" + IP + "/school5/getTeacherCollege.html";
		/* String uriAPI = "http://" + IP + "/school5/news/45/client.html"; */
		HttpEntity entity = HttpUtils.getHttpEntity(urlname, 2);
		String jsonString = "";
		InputStream in = HttpUtils.getInputStream(entity);
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				jsonString = sb.toString();
				Log.i("jsonString", jsonString);
				return jsonString;

			} catch (Exception e) {

				e.printStackTrace();
				return jsonString;
			}
		}
		return jsonString;
	}

	@Override
	public JSONObject sent2MapServer(JSONObject obj) {

		// String uriAPI = "http://" + IP + "/JWGL_Server_1/LoginServlet";
		String uriAPI = "http://" + IP + "/school5/mapInStudent.html";

		// String strResult = "FAIL";

		Log.i("JSON", obj.toString());
		HttpEntity entity = HttpUtils.getHttpEntity(uriAPI, 2, obj);
		JSONObject jo2 = new JSONObject();
		Log.i("JSON", jo2.toString());
		InputStream in = HttpUtils.getInputStream(entity);
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				jo2 = new JSONObject(sb.toString());
				Log.i("MAP", "jsonObject=" + jo2.toString());
				return jo2;

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Log.i("JSON", jo2.toString());
		return jo2;

	}

	@Override
	public JSONObject linkServer(JSONObject obj, String URL) {

		Log.i("JSON", obj.toString());
		HttpEntity entity = HttpUtils.getHttpEntity(URL, 2, obj);
		JSONObject jo2 = new JSONObject();
		Log.i("JSON", jo2.toString());
		InputStream in = HttpUtils.getInputStream(entity);
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder sb = new StringBuilder();
				String s = null;
				while ((s = br.readLine()) != null) {
					sb.append(s);
				}
				jo2 = new JSONObject(sb.toString());
				Log.i("JSON", jo2.toString());
				return jo2;

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Log.i("JSON", jo2.toString());
		return jo2;

	}

}
