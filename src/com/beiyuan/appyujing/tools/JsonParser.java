package com.beiyuan.appyujing.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.text.TextUtils;
import android.util.Log;

import com.beiyuan.appyujing.data.StudentData;

public class JsonParser {
	/**
	 * 
	 * 
	 * @param json
	 * @return
	 */
	public static String parseIatResult(String json) {
		if (TextUtils.isEmpty(json))
			return "";

		StringBuffer ret = new StringBuffer();
		try {
			JSONTokener tokener = new JSONTokener(json);
			JSONObject joResult = new JSONObject(tokener);

			JSONArray words = joResult.getJSONArray("ws");
			for (int i = 0; i < words.length(); i++) {
				//
				JSONArray items = words.getJSONObject(i).getJSONArray("cw");
				JSONObject obj = items.getJSONObject(0);
				ret.append(obj.getString("w"));
				//
				// for(int j = 0; j < items.length(); j++)
				// {
				// JSONObject obj = items.getJSONObject(j);
				// ret.append(obj.getString("w"));
				// }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret.toString();
	}

	/**
	 * 
	 * 点名考勤模块解析学生数据
	 * 
	 * @param json
	 * @return
	 */
	public static List<StudentData> parseStudentResult(JSONObject json) {
		List<StudentData> listStrudents = new ArrayList<StudentData>();
		if (TextUtils.isEmpty(json.toString())) {
			return listStrudents;
		} else {

			try {
				for (int i = 0; i < json.length(); i++) {
					StudentData studentEntity = new StudentData();
					String classarray = json.getString("" + i);
					JSONObject jsonclass = new JSONObject(classarray);
					String name = jsonclass.getString("studentName");
					String number = jsonclass.getString("studentID");
					String photo = jsonclass.getString("headSculpture");
					studentEntity.setNumber(number);
					studentEntity.setName(name);
					studentEntity.setPhoto(photo);
					studentEntity.setStatus("签到");
					listStrudents.add(studentEntity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return listStrudents;
		}
	}

	/**
	 * 
	 * 更新模块解析学生数据
	 * 
	 * @param json
	 * @return
	 */
	public static List<StudentData> parseOneStudentResult(JSONObject json) {
		List<StudentData> listOneStrudents = new ArrayList<StudentData>();
		if (TextUtils.isEmpty(json.toString())) {
			return listOneStrudents;
		} else {

			try {
				StudentData studentEntity = new StudentData();
				String name = json.getString("studentName");
				String status = json.getString("studyStatus");
				String number = json.getString("studentNumber");
				StudentData studentData = new StudentData();
				studentData.setNumber(number);
				studentData.setName(name);
				studentData.setStatus(status);
				studentData.setOldStatus(status);
				listOneStrudents.add(studentData);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return listOneStrudents;
		}
	}

	/**
	 * 
	 * 考勤查询模块解析学生数据
	 * 
	 * @param json
	 * @return
	 */
	public static List<StudentData> parseQueryStudentResult(JSONObject json) {
		List<StudentData> listStrudents = new ArrayList<StudentData>();
		if (TextUtils.isEmpty(json.toString())) {
			return listStrudents;
		} else {

			try {
				for (int i = 0; i < json.length(); i++) {
					StudentData studentEntity = new StudentData();
					String classarray = json.getString("" + i);
					JSONObject jsonclass = new JSONObject(classarray);
					String name = jsonclass.getString("studentName");
					String number = jsonclass.getString("studentNumber");
					String status = jsonclass.getString("studyStatus");
					studentEntity.setNumber(number);
					studentEntity.setName(name);
					studentEntity.setStatus(status);
					listStrudents.add(studentEntity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return listStrudents;
		}
	}

	/**
	 * 
	 * 快速考勤查询模块解析学生数据
	 * 
	 * @param json
	 * @return
	 */
	public static List<StudentData> parseQuickQueryStudentResult(JSONObject json) {
		List<StudentData> listStrudents = new ArrayList<StudentData>();
		if (TextUtils.isEmpty(json.toString())) {
			return listStrudents;
		} else {

			try {
				String studentlist = json.getString("callOverCountUtilList");
				JSONArray studentList = new JSONArray(studentlist);
				for (int i = 0; i < studentList.length(); i++) {
					JSONObject temp = (JSONObject) studentList.get(i);
					StudentData studentEntity = new StudentData();
					String name = temp.getString("studentName");
					String number = temp.getString("studentNumber");
					String status = temp.getString("statusNum");
					studentEntity.setNumber(number);
					studentEntity.setName(name);
					studentEntity.setStatus(status);
					listStrudents.add(studentEntity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("mytag", "listStrudents==" + listStrudents.size());
			return listStrudents;
		}
	}

	/**
	 * 
	 * 学生详细信息查询模块解析学生数据
	 * 
	 * @param json
	 * @return
	 */
	public static String parseStudentInfoResult(JSONObject json,
			String studentName) {
		String allName = null;
		if (TextUtils.isEmpty(json.toString())) {
			return null;
		} else {

			try {
				// String studentlist = json.getString("grade");

				String gradeName = json.getString("yearClass");
				String profession = json.getString("profession");
				String classId = json.getString("classId");
				allName = gradeName + profession + classId + "班";

			} catch (Exception e) {
				e.printStackTrace();
			}
			return allName;
		}
	}

	/**
	 * 学生详细信息查询模块解析学生考勤数据
	 * 
	 * @param json
	 * @return
	 */
	public static List<StudentData> parseStudentStatusResult(
			JSONObject json) {
		List<StudentData> studentStatus = new ArrayList<StudentData>();
		if (TextUtils.isEmpty(json.toString())) {
			return null;
		} else {

			try {
				String studentlist = json.getString("callOver");
				Log.i("mytag", "callover===" + studentlist);
				JSONArray Status = new JSONArray(studentlist);
				for (int i = 0; i < Status.length(); i++) {
					HashMap<String, String> map = new HashMap<String, String>();
					StudentData studentEntity = new StudentData();
					JSONObject temp = (JSONObject) Status.get(i);
					String dateTxt = temp.getString("date");
					String tname = temp.getString("teacherName");
					String coursename = temp.getString("className");
					String year  = Util.StrToYear(dateTxt);
					String month = Util.StrToMonth(dateTxt);
					String date = Util.StrToDate(dateTxt);
					String yearMonth = year +"年"+month+"月";
					studentEntity.setDateMonth(yearMonth);
					studentEntity.setDate(date);
				    studentEntity.setCourseName(coursename);
				    studentEntity.setTeacherName(tname);
				    studentStatus.add(studentEntity);
				    }
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("mytag", "studentStatus==" + studentStatus.size());
			return studentStatus;
		}
	}

	/**
	 * 封装成JSONObject
	 * 
	 * @param listStrudents
	 * @param lessonName
	 * @return
	 */
	public static JSONObject packageJsonObject(List<StudentData> listStrudents,
			String lessonName) {
		JSONObject jsonObject = new JSONObject();

		if (listStrudents.size() == 0) {
			return jsonObject;
		} else {
			try {
				Map map = new HashMap();
				map.clear();
				for (int i = 0; i < listStrudents.size(); i++) {

					if (!listStrudents.get(i).getStatus().equals("签到")) {
						JSONObject obj = new JSONObject();
						obj.put("studentNumber", listStrudents.get(i)
								.getNumber());
						obj.put("status", listStrudents.get(i).getStatus());
						obj.put("classTime", lessonName);
						map.put(i + "", obj);
					}
				}
				jsonObject = new JSONObject(map);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return jsonObject;
		}
	}

	/**
	 * 
	 * 学生查看个人信息
	 * 
	 * @param json
	 * @return
	 */
	public static List<HashMap<String, String>> parseUpdStuInfoResult(
			JSONObject json) {
		List<HashMap<String, String>> studentInfo = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		HashMap<String, String> map2 = new HashMap<String, String>();
		HashMap<String, String> map3 = new HashMap<String, String>();
		HashMap<String, String> map4 = new HashMap<String, String>();
		HashMap<String, String> map5 = new HashMap<String, String>();
		HashMap<String, String> map6 = new HashMap<String, String>();
		HashMap<String, String> map7 = new HashMap<String, String>();

		if (TextUtils.isEmpty(json.toString())) {
			return studentInfo;
		} else {
			try {
				String name = json.getString("name");

				String studentId = json.getString("studentNumber");
				Log.i("UPDATEINFO", "studentId===" + studentId);
				String phoneNumber = json.getString("phone");
				String idCard = json.getString("cardId");
				String colleage = json.getString("college");
				String grade = json.getString("grade");
				String profession = json.getString("profession");
				String sClass = json.getString("class");
				map1.put("user_name", "姓名:");
				map1.put("user_id", name);
				map2.put("user_name", "学号:");
				map2.put("user_id", studentId);
				map3.put("user_name", "身份证号:");
				map3.put("user_id", idCard);
				map4.put("user_name", "学院:");
				map4.put("user_id", colleage);
				map5.put("user_name", "年级:");
				map5.put("user_id", grade);
				map6.put("user_name", "专业:");
				map6.put("user_id", profession);
				map7.put("user_name", "班级:");
				map7.put("user_id", sClass);

				studentInfo.add(map1);
				studentInfo.add(map2);
				studentInfo.add(map3);
				studentInfo.add(map4);
				studentInfo.add(map5);
				studentInfo.add(map6);
				studentInfo.add(map7);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("mytag", "studentInfo==" + studentInfo.size());
			return studentInfo;
		}
	}

	/**
	 * 
	 * 学生查看可修改信息
	 * 
	 * @param json
	 * @return
	 */
	public static List<HashMap<String, String>> parseUpdInfoResult(
			JSONObject json) {
		List<HashMap<String, String>> studentInfo = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		HashMap<String, String> map2 = new HashMap<String, String>();

		if (TextUtils.isEmpty(json.toString())) {
			return studentInfo;
		} else {
			try {
				String phoneNumber = json.getString("phone");

				map1.put("user_name", "手机号码:");
				map1.put("user_id", phoneNumber);
				map2.put("user_name", "密码:");
				map2.put("user_id", "**********");
				studentInfo.add(map1);
				studentInfo.add(map2);

			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("mytag", "studentInfo==" + studentInfo.size());
			return studentInfo;
		}
	}

	/**
	 * 
	 * 辅导员查看个人信息
	 * 
	 * @param json
	 * @return
	 */
	public static List<HashMap<String, String>> parseUpdHeadTeaInfoResult(
			JSONObject json) {
		List<HashMap<String, String>> headInfo = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> map1 = new HashMap<String, String>();
		HashMap<String, String> map2 = new HashMap<String, String>();
		HashMap<String, String> map3 = new HashMap<String, String>();
		HashMap<String, String> map4 = new HashMap<String, String>();
		StringBuffer sb = new StringBuffer();
		if (TextUtils.isEmpty(json.toString())) {
			return headInfo;
		} else {
			try {
				String name = json.getString("name");
				String idCard = json.getString("cardId");
				String colleage = json.getString("college");
				String profession = json.getString("gradeList");
				Log.i("mytag", "-------" + profession);
				JSONArray projson = new JSONArray(profession);
				map1.put("user_name", "姓名:");
				map1.put("user_id", name);
				map2.put("user_name", "身份证号:");
				map2.put("user_id", idCard);
				map3.put("user_name", "学院:");
				map3.put("user_id", colleage);
				for (int i = 0; i < projson.length(); i++) {

					JSONObject temp = (JSONObject) projson.get(i);
					String grade = temp.getString("yearClass");
					String professionTxt = temp.getString("profession");
					String classTxt = temp.getString("classId");
					String allName = grade + professionTxt + classTxt
							+ "班     ";
					sb.append(allName);
				}
				map4.put("user_name", "班级:");
				map4.put("user_id", sb.toString());

				headInfo.add(map1);
				headInfo.add(map2);
				headInfo.add(map3);
				headInfo.add(map4);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.i("mytag", "studentInfo==" + headInfo.size());
			return headInfo;
		}
	}

	
}
