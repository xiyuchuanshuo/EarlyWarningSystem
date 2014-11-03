package com.beiyuan.appyujing.activity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.adapter.QueryNameListAdapter;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.StudentData;
import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.JsonParser;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.DateTimePickerDialog;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.DateTimePickerDialog.OnDateTimeSetListener;
import com.beiyuan.appyujing.view.EditClick;
import com.beiyuan.appyujing.view.EditClick.OnButtonClickListener;
import com.beiyuan.appyujing.view.TitleViewNew;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;
import com.beiyuan.appyujing.view.TitleViewNew.OnBackBtnClickListener;
import com.beiyuan.appyujing.view.TitleViewNew.OnDialogBtnClickListener;

/**
 * 精确查询功能
 * 
 * @author juan
 * 
 */
public class QueryNameActivity extends MyActivity {

	private TitleView mTitle;
	private final static String TAG = "QueryNameActivity";
	private Dialog builder = null;
	private Handler handler;

	private ArrayList<String> listCollege = new ArrayList<String>();
	private ArrayList<String> listGrade = new ArrayList<String>();
	private ArrayList<String> listProfession = new ArrayList<String>();
	private ArrayList<String> listClass = new ArrayList<String>();
	private ArrayList<String> listCourse = new ArrayList<String>();
	private ArrayList<String> listLesson = new ArrayList<String>();

	private EditClick editCollege;
	private EditClick editGrade;
	private EditClick editProfession;
	private EditClick editClass;
	private EditClick editCourse;
	private EditClick editLesson;
	private EditClick editDate;

	private JSONObject obj;

	private EmergencyHelper emergencyHelper = new EmergencyHelper(this);
	private ListView attend_list;
	private List<StudentData> listItem = new ArrayList<StudentData>();
	private List<StudentData> listStrudents = new ArrayList<StudentData>();
	private QueryNameListAdapter listViewAdapter;
	private String courseName;
	private String lessonName;
	private Bundle bundleb;
	private UrlService urlService = new UrlServiceImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_name);
		initView();
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					Tools.mToast(QueryNameActivity.this, "返回空值");
					break;
				case 5:
					Tools.mToast(QueryNameActivity.this, "无网络连接");
					break;
				}
			}
		};
	}

	public void initView() {
		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.query_title);
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(R.drawable.side_btn_img);
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				getCollegeData();
				showCheckDialog();
			}

		});
		listLesson.add("1");
		listLesson.add("2");
		listLesson.add("3");
		listLesson.add("4");
		listLesson.add("5");
		listLesson.add("6");
		listLesson.add("7");
		listLesson.add("8");
		listLesson.add("9");
		listLesson.add("10");

		attend_list = (ListView) findViewById(R.id.query_name_list);
		listViewAdapter = new QueryNameListAdapter(this, listItem); // 创建适配器
		attend_list.setAdapter(listViewAdapter);
		listViewAdapter.notifyDataSetChanged();

	}

	/**
	 * 刷新
	 */
	private Handler handlerSecond = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);
			bundleb = msg.getData();
			listItem = (List<StudentData>) bundleb.get("listItem");
			Log.i(TAG, "listItem2===" + listItem.size());
			listViewAdapter.updateListView(listItem);
		}
	};

	/**
	 * 教师选择对话框
	 */
	private void showCheckDialog() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.query_check_dialog, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		editCollege = (EditClick) viewDialog.findViewById(R.id.check_college);
		editGrade = (EditClick) viewDialog.findViewById(R.id.check_grade);
		editProfession = (EditClick) viewDialog
				.findViewById(R.id.check_profession);
		editClass = (EditClick) viewDialog.findViewById(R.id.check_class);
		editCourse = (EditClick) viewDialog.findViewById(R.id.check_course);
		editLesson = (EditClick) viewDialog.findViewById(R.id.check_lesson);
		editDate = (EditClick) viewDialog.findViewById(R.id.check_date);
		editCollege.setHint("请选择学院");
		editGrade.setHint("请选择年级");
		editProfession.setHint("请选择专业");
		editClass.setHint("请选择班级");
		editCourse.setHint("请选择课程");
		editLesson.setHint("请选择节数");
		editDate.setHint("请选择日期");
		builder.setCancelable(false);
		builder.setTitle("选择班级对话框");
		builder.setView(viewDialog);
		editCollege.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showCollegeDialog();
			}
		});
		editGrade.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showGradeDialog();

			}
		});
		editProfession.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showProfessionDialog();
			}
		});
		editClass.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showClassDialog();
			}
		});
		editCourse.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showCourseDialog();
			}
		});
		editLesson.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showLessonDialog();
			}
		});
		editDate.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showDataDialog();
			}
		});

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (editCourse.getText().toString().equals("")) {

					Tools.mToast(QueryNameActivity.this, "课程不能为空");
					return;
				}
				if (editLesson.getText().toString().equals("")) {
					Tools.mToast(QueryNameActivity.this, "节数不能为空");
					return;
				}
				if (editDate.getText().toString().equals("")) {
					Tools.mToast(QueryNameActivity.this, "日期不能为空");
					return;
				}
				getStudentData();

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show();
	}

	/**
	 * 教师选择学院
	 */
	@SuppressLint("NewApi")
	public void showCollegeDialog() {

		builder = new Dialog(this);
		builder.setTitle("学院列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterCollege = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listCollege);

		listView_college.setAdapter(adapterCollege);
		adapterCollege.notifyDataSetChanged();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String collegeName = text.getText().toString();
				Log.i(TAG, "collegeName======" + collegeName);
				editCollege.setText(collegeName);
				builder.dismiss();
			}
		});
	}

	/**
	 * 教师选择年级
	 */
	@SuppressLint("NewApi")
	public void showGradeDialog() {

		builder = new Dialog(this);
		builder.setTitle("年级列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_grade = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterGrade = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listGrade);

		listView_grade.setAdapter(adapterGrade);
		adapterGrade.notifyDataSetChanged();
		listView_grade.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String gradeName = text.getText().toString();
				Log.i(TAG, "gradeName======" + gradeName);
				editGrade.setText(gradeName);
				builder.dismiss();
				getProfessionData();

			}

		});

	}

	/**
	 * 教师选择专业
	 */
	@SuppressLint("NewApi")
	public void showProfessionDialog() {
		if (editCollege.getText().toString().equals("")) {
			Tools.mToast(QueryNameActivity.this, "学院不能为空");
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			Tools.mToast(QueryNameActivity.this, "年级不能为空");
			return;
		}
		builder = new Dialog(this);
		builder.setTitle("专业列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_profession = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterProfession = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listProfession);

		listView_profession.setAdapter(adapterProfession);
		adapterProfession.notifyDataSetChanged();
		listView_profession.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String professionName = text.getText().toString();
				Log.i(TAG, "professionName======" + professionName);
				editProfession.setText(professionName);
				builder.dismiss();
				getClassData();
			}

		});

	}

	/**
	 * 教师选择班级
	 */
	@SuppressLint("NewApi")
	public void showClassDialog() {

		if (editCollege.getText().toString().equals("")) {
			Tools.mToast(QueryNameActivity.this, "学院不能为空");
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			Tools.mToast(QueryNameActivity.this, "年级不能为空");
			return;
		}
		if (editProfession.getText().toString().equals("")) {
			Tools.mToast(QueryNameActivity.this, "专业不能为空");
			return;
		}
		builder = new Dialog(this);
		builder.setTitle("班级列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_class = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listClass);

		listView_class.setAdapter(adapterClass);
		adapterClass.notifyDataSetChanged();
		listView_class.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String className = text.getText().toString();
				Log.i(TAG, "className======" + className);
				editClass.setText(className);
				builder.dismiss();
				getCourseData();

			}

		});

	}

	/**
	 * 教师选择课程
	 */
	@SuppressLint("NewApi")
	public void showCourseDialog() {

		builder = new Dialog(this);
		builder.setTitle("课程列表");
		builder.show();

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.college_dialog, null);
		Display display = this.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		// 设置对话框的宽高
		LayoutParams layoutParams = new LayoutParams(width * 90 / 100, 350);
		builder.setContentView(viewDialog, layoutParams);

		ListView listView_course = (ListView) viewDialog
				.findViewById(R.id.college_listview);

		ArrayAdapter<String> adapterCourse = new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1, listCourse);

		listView_course.setAdapter(adapterCourse);
		adapterCourse.notifyDataSetChanged();
		listView_course.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String cName = text.getText().toString();
				Log.i(TAG, "courseName======" + cName);
				editCourse.setText(cName);
				builder.dismiss();

			}

		});

	}

	// 教师多选节数
	public void showLessonDialog() {

		final String[] mItems = new String[listLesson.size()];
		// final boolean[] a = new boolean[listGrade.size()];
		for (int i = 0; i < listLesson.size(); i++) {
			mItems[i] = listLesson.get(i);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(
				QueryNameActivity.this);

		builder.setTitle("多项选择");
		final boolean[] a = new boolean[listLesson.size()];
		for (int i = 0; i < a.length; i++) {
			a[i] = false;
		}
		builder.setMultiChoiceItems(mItems, a,
				new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialog,
							int whichButton, boolean isChecked) {
						a[whichButton] = isChecked;

					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				StringBuilder stringBuilder = new StringBuilder();
				for (int i = 0; i < a.length; i++) {
					if (a[i] == true) {
						stringBuilder.append(mItems[i] + "、");
					}
				}
				editLesson.setText(stringBuilder.toString());
				lessonName = editLesson.getText().toString()
						.replaceAll(",", "");
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.create().show();
	}

	/**
	 * 显示日期
	 */
	public void showDataDialog() {
		DateTimePickerDialog dialog = new DateTimePickerDialog(this,
				System.currentTimeMillis());
		dialog.setOnDateTimeSetListener(new OnDateTimeSetListener() {
			public void OnDateTimeSet(AlertDialog dialog, long date) {
				Tools.mToast(QueryNameActivity.this, "您输入的日期是："
						+ getStringDate(date));
				editDate.setText(getStringDate(date));

			}
		});
		dialog.show();
	}

	/**
	 * 将长时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getStringDate(Long date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(date);
		return dateString;
	}

	/**
	 * 获取学院、年级数据
	 */
	public void getCollegeData() {
		// 开线程，从服务端获取学院、年级数据
		Thread threadGetCollege = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				String jsonToString = urlService
						.getJsonContent(ConstantData.GETCOLLEGEGRA);
				listCollege = (ArrayList<String>) urlService.getCollegeList(
						"college", jsonToString);
				listGrade = (ArrayList<String>) urlService.getCollegeList(
						"grade", jsonToString);
				Log.i(TAG, "listString===" + listCollege);
				Log.i(TAG, "listString===" + listGrade);

			}
		});
		threadGetCollege.start();
	}

	/**
	 * 学生向服务端发送学院年级,获取专业
	 */
	public void getProfessionData() {
		obj = new JSONObject();
		try {
			if (editCollege.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "学院不能为空");
				return;
			}
			if (editGrade.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "年级不能为空");
				return;
			}
			obj.put("collegeName", editCollege.getText().toString().trim());
			obj.put("yearClass", editGrade.getText().toString().trim());

			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadQuery = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonResult = urlService.sentParams2DataServer(
							obj, ConstantData.GETPROFESSION);
					Log.i("JSON", "jsonRegeditRst" + jsonResult);

					listProfession.clear();
					try {
						JSONArray jsonArray = jsonResult
								.getJSONArray("profession");
						for (int i = 0; i < jsonArray.length(); i++) {
							String msg = jsonArray.getString(i);

							listProfession.add(msg);
						}
						Log.i("mytag", "listProfession=========="
								+ listProfession);

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}
			});
			threadQuery.start();

		} catch (Exception e) {
			Tools.mToast(QueryNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
		}
	}

	/**
	 * 向服务端发送学院年级专业,获取班级
	 */
	public void getClassData() {

		obj = new JSONObject();
		try {
			if (editCollege.getText().toString().equals("")) {

				Tools.mToast(QueryNameActivity.this, "学院不能为空");
				return;
			}
			if (editGrade.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "年级不能为空");
				return;
			}
			if (editProfession.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "专业不能为空");
				return;
			}
			obj.put("collegeName", editCollege.getText().toString().trim());
			obj.put("yearClass", editGrade.getText().toString().trim());
			obj.put("profession", editProfession.getText().toString().trim());

			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadQuery = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonRegisterResult = urlService
							.sentParams2DataServer(obj, ConstantData.GETCLASS);
					Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

					listClass.clear();
					try {
						JSONArray jsonArray = jsonRegisterResult
								.getJSONArray("classID");
						for (int i = 0; i < jsonArray.length(); i++) {
							String msg = jsonArray.getString(i);
							listClass.add(msg);
						}
						Log.i(TAG, "listClass===" + listClass);

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}
			});
			threadQuery.start();
		} catch (Exception e) {
			Tools.mToast(QueryNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
		}
	}

	/**
	 * 向服务端发送学院年级、专业、班级,获取课程
	 */
	public void getCourseData() {

		obj = new JSONObject();
		try {
			if (editGrade.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "年级不能为空");
				return;
			}
			if (editProfession.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "专业不能为空");
				return;
			}
			if (editClass.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "班级不能为空");
				return;
			}
			obj.put("grade", editGrade.getText().toString().trim());
			obj.put("profession", editProfession.getText().toString().trim());
			obj.put("classID", editClass.getText().toString().trim());

			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadQuery = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonRegisterResult = urlService
							.sentParams2DataServer(obj, ConstantData.GETCOURSE);
					Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

					JSONArray jsonArray;
					listCourse.clear();
					try {
						jsonArray = jsonRegisterResult
								.getJSONArray("className");
						for (int i = 0; i < jsonArray.length(); i++) {
							String msg = jsonArray.getString(i);
							listCourse.add(msg);
						}
						Log.i(TAG, "listCourse===" + listCourse);

					} catch (JSONException e) {
						e.printStackTrace();

						handler.sendEmptyMessage(5);
						return;
					}
					if (jsonArray.equals("")) {
						listCourse.add("");
						handler.sendEmptyMessage(1);
					}
				}
			});
			threadQuery.start();

		} catch (Exception e) {
			Tools.mToast(QueryNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
		}
	}

	/**
	 * 辅导员向服务端发送课程、节数、日期,获取学生所有信息：photo 姓名，学号，考勤状况
	 */
	public void getStudentData() {
		// attendSheet.deletetable(emergencyHelper);
		obj = new JSONObject();
		try {
			if (editCollege.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "学院不能为空");
				return;
			}
			if (editGrade.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "年级不能为空");
				return;
			}
			if (editProfession.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "专业不能为空");
				return;
			}
			if (editClass.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "班级不能为空");
				return;
			}
			if (editCourse.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "课程不能为空");
				return;
			}
			if (editLesson.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "节数不能为空");
				return;
			}
			if (editDate.getText().toString().equals("")) {
				Tools.mToast(QueryNameActivity.this, "日期不能为空");
				return;
			}
			obj.put("grade", editGrade.getText().toString().trim());
			obj.put("profession", editProfession.getText().toString().trim());
			obj.put("classID", editClass.getText().toString().trim());
			obj.put("course", editCourse.getText().toString().trim());
			obj.put("classTime",
					editLesson.getText().toString().replaceAll("、", "").trim());
			obj.put("date", editDate.getText().toString().trim());
			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadQuery = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonQueryResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.GETQUERYATTENDSTUDENT);
					Log.i("JSON", "jsonRegeditRst" + jsonQueryResult);

					listStrudents = JsonParser
							.parseQueryStudentResult(jsonQueryResult);

					Message msg = new Message();
					Bundle b = new Bundle();// 存放数据
					Log.e(TAG, "listStrudents======" + listStrudents.size());

					b.putSerializable("listItem", (Serializable) listStrudents);
					msg.setData(b);
					handlerSecond.sendMessage(msg);

				}
			});
			threadQuery.start();

		} catch (Exception e) {
			Tools.mToast(QueryNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
		}
	}

	/**
	 * 此方法必须重写，以决绝退出activity时 dialog未dismiss而报错的bug
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			builder.dismiss();
			builder.cancel();
		} catch (Exception e) {
			System.out.println("Dialog取消，失败！");
			// TODO: handle exception
		}
		super.onDestroy();
	}
}
