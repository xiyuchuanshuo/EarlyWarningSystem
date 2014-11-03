package com.beiyuan.appyujing.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Point;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.adapter.CallNameListAdapter;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.data.StudentData;
import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.JsonParser;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.ActionItem;
import com.beiyuan.appyujing.view.EditClick;
import com.beiyuan.appyujing.view.EditClick.OnButtonClickListener;
import com.beiyuan.appyujing.view.TitlePopup;
import com.beiyuan.appyujing.view.TitlePopup.OnItemOnClickListener;
import com.beiyuan.appyujing.view.TitleViewCopy;
import com.beiyuan.appyujing.view.TitleViewCopy.OnBackBtnClickListener;
import com.beiyuan.appyujing.view.TitleViewCopy.OnCompleteBtnClickListener;
import com.beiyuan.appyujing.view.TitleViewCopy.OnDialogBtnClickListener;

/**
 * 点名考勤:教师点名功能，选择相应的班级进行点名，点完名字后上传数据
 * 
 * @author juan
 * 
 */
public class CallNameActivity extends MyActivity implements
		OnItemOnClickListener {

	private TitleViewCopy mTitle;
	private final static String TAG = "CallNameActivity";
	private Dialog builder = null;
	private Handler handler;
	private ArrayList<String> listCollege = new ArrayList<String>();
	private ArrayList<String> listGrade = new ArrayList<String>();
	private ArrayList<String> listProfession = new ArrayList<String>();
	private ArrayList<String> listClass = new ArrayList<String>();
	private ArrayList<String> listCourse = new ArrayList<String>();
	private ArrayList<String> listLesson = new ArrayList<String>();
	private ArrayList<String> listData = new ArrayList<String>();

	private EditClick editCollege;
	private EditClick editGrade;
	private EditClick editProfession;
	private EditClick editClass;
	private EditClick editCourse;
	private EditClick editLesson;
	private EditText editNum;
	private ProgressDialog pdialog;
	private JSONObject obj;
	private UrlService urlService = new UrlServiceImpl();
	private EmergencyHelper emergencyHelper = new EmergencyHelper(this);
	private ListView attend_list;
	private CallNameListAdapter listViewAdapter;
	private String result;
	private String courseName;
	private String lessonName;
	private TitlePopup titlePopup;
	private String flag = "";
	private List<StudentData> listStrudents = new ArrayList<StudentData>();
	private List<StudentData> listOneStrudents = new ArrayList<StudentData>();
	private List<StudentData> listItem = new ArrayList<StudentData>();
	
	private Bundle bundleb;
	private Bundle data;
	ArrayAdapter<String> adapterCollege;
	ListView listView_college;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.call_name_copy);
		initView();

		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					Tools.mToast(CallNameActivity.this, "上传成功");
					listViewAdapter.notifyDataSetChanged();
					break;
				case 2:
					Tools.mToast(CallNameActivity.this, "上传失败");
					break;
				case 3:
					Tools.mToast(CallNameActivity.this, "获取成功");
					break;
				case 5:
					Tools.mToast(CallNameActivity.this, "无网络连接");
					break;
				}
			}
		};
	}

	public void initView() {
		initTitlePopup();
		mTitle = (TitleViewCopy) findViewById(R.id.title);
		mTitle.setTitle("点名考勤");
		mTitle.setLeftButton(new OnBackBtnClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				Tools.mToast(CallNameActivity.this, "可以点击");
				finish();
			}

		});
		// 完成
		mTitle.setRightButton(new OnCompleteBtnClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				Log.i(TAG, "flag===" + flag);
				if (flag.equals("1")) {
					setAttendData();
				} else if (flag.equals("2")) {
					setOneAttendData();
				} else {
					Tools.mToast(CallNameActivity.this, "没有任何数据需要上传");
				}

			}
		});
		// 显示dialog，用于选择学生数据
		mTitle.setDialogButton(new OnDialogBtnClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				titlePopup.show(button);
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

		attend_list = (ListView) findViewById(R.id.call_name_list);
		listViewAdapter = new CallNameListAdapter(this, listStrudents); // 创建适配器
		attend_list.setAdapter(listViewAdapter);
		listViewAdapter.notifyDataSetChanged();

	}

	public void initTitlePopup() {
		titlePopup = new TitlePopup(this, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		titlePopup.addAction(new ActionItem(this, "点名"));
		titlePopup.addAction(new ActionItem(this, "更新"));
		titlePopup.setItemOnClickListener(this);
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
			pdialog.dismiss();
		}
	};
	/**
	 * 刷新
	 */
	private Handler handlerTool = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			data = msg.getData();
			listData = (ArrayList<String>) data.get("listData");
			Log.i(TAG, "listData===" + listData.size());
			adapterCollege = new ArrayAdapter<String>(
					CallNameActivity.this,
					android.R.layout.simple_expandable_list_item_1, listData);
			listView_college.setAdapter(adapterCollege);
			pdialog.dismiss();
		}
	};

	/**
	 * 教师选择对话框
	 */
	private void showCheckDialog() {
		
		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.check_dialog, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
	
		editCollege = (EditClick) viewDialog.findViewById(R.id.check_college);
		editGrade = (EditClick) viewDialog.findViewById(R.id.check_grade);
		editProfession = (EditClick) viewDialog
				.findViewById(R.id.check_profession);
		editClass = (EditClick) viewDialog.findViewById(R.id.check_class);
		editCourse = (EditClick) viewDialog.findViewById(R.id.check_course);
		editLesson = (EditClick) viewDialog.findViewById(R.id.check_lesson);
		
		editCollege.setHint("请选择学院");
		editGrade.setHint("请选择年级");
		editProfession.setHint("请选择专业");
		editClass.setHint("请选择班级");
		editCourse.setHint("请选择课程");
		editLesson.setHint("请选择节数");

		editCollege.setEditable(false);
		editGrade.setEditable(false);
		editProfession.setEditable(false);
		editClass.setEditable(false);
		editLesson.setEditable(false);
		
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
				courseName = editCourse.getText().toString();
			}
		});
		editLesson.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showLessonDialog();
			}
		});

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (editCourse.getText().toString().equals("")) {
					Tools.mToast(CallNameActivity.this, "课程不能为空");
					return;
				}
				if (editLesson.getText().toString().equals("")) {
					Tools.mToast(CallNameActivity.this, "节数不能为空");
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
	 * 教师更新学生考勤状况
	 */
	public void showUpdateDateDialog() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.update_attend, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		editLesson = (EditClick) viewDialog.findViewById(R.id.check_lesson);
		editNum = (EditText) viewDialog.findViewById(R.id.input_studentnum);
		editLesson.setHint("请选择节数");
		builder.setCancelable(false);
		builder.setTitle("选择学生对话框");
		builder.setView(viewDialog);
		editLesson.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showLessonDialog();
			}
		});

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (editLesson.getText().toString().equals("")) {
					Tools.mToast(CallNameActivity.this, "节数不能为空");
					return;
				}
				if (editNum.getText().toString().equals("")) {
					Tools.mToast(CallNameActivity.this, "学号不能为空");
					return;
				}
				getOneStudentData();

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

		listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

//		ArrayAdapter<String> adapterCollege = new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, listCollege);
//
//		listView_college.setAdapter(adapterCollege);
		getCollegeData();
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
//		adapterCollege.notifyDataSetChanged();

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
//				getProfessionData();

			}

		});
		adapterGrade.notifyDataSetChanged();

	}

	/**
	 * 教师选择专业
	 */
	@SuppressLint("NewApi")
	public void showProfessionDialog() {
		if (editCollege.getText().toString().equals("")) {
			Tools.mToast(CallNameActivity.this, "学院不能为空");
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			Tools.mToast(CallNameActivity.this, "年级不能为空");
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

	    listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);
		getProfessionData();

		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String professionName = text.getText().toString();
				Log.i(TAG, "professionName======" + professionName);
				editProfession.setText(professionName);
				builder.dismiss();
			}

		});
	}

	/**
	 * 教师选择班级
	 */
	@SuppressLint("NewApi")
	public void showClassDialog() {

		if (editCollege.getText().toString().equals("")) {
			Tools.mToast(CallNameActivity.this, "学院不能为空");
			return;
		}
		if (editGrade.getText().toString().equals("")) {
			Tools.mToast(CallNameActivity.this, "年级不能为空");
			return;
		}
		if (editProfession.getText().toString().equals("")) {
			Tools.mToast(CallNameActivity.this, "专业不能为空");
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

		listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

//		ArrayAdapter<String> adapterClass = new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, listClass);
//
//		listView_class.setAdapter(adapterClass);
		getClassData();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String className = text.getText().toString();
				Log.i(TAG, "className======" + className);
//				getCourseData();
				editClass.setText(className);
				builder.dismiss();

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

		listView_college = (ListView) viewDialog
				.findViewById(R.id.college_listview);

//		ArrayAdapter<String> adapterCourse = new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, listCourse);
//
//		listView_course.setAdapter(adapterCourse);
//		adapterCourse.notifyDataSetChanged();
		getCourseData();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

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
		for (int i = 0; i < listLesson.size(); i++) {
			mItems[i] = listLesson.get(i);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(
				CallNameActivity.this);

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
						.replaceAll("、", "");
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.create().show();
	}

	/**
	 * 获取学院、年级数据
	 */
	public void getCollegeData() {
		// 开线程，从服务端获取学院、年级数据
		pdialog = Tools.pd(CallNameActivity.this,"正在获取");
		Thread threadRegister = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				String jsonToString = urlService
						.getJsonContent(ConstantData.GETCOLLEGEGRA);
				listCollege = (ArrayList<String>) urlService.getCollegeList(
						"college", jsonToString);
				listGrade = (ArrayList<String>) urlService.getCollegeList(
						"grade", jsonToString);
				
				Message msg = new Message();
				Bundle b = new Bundle();// 存放数据
				b.putSerializable("listData",
						(Serializable) listCollege);
				msg.setData(b);
				handlerTool.sendMessage(msg);
				Log.i(TAG, "listString===" + listCollege);
				Log.i(TAG, "listString===" + listGrade);

			}
		});
		threadRegister.start();
	}

	/**
	 * 学生向服务端发送学院年级,获取专业
	 */
	public void getProfessionData() {
		obj = new JSONObject();
		try {
			if (!editCollege.getText().toString().equals("")
					&& !editGrade.getText().toString().equals("")) {
				obj.put("collegeName", editCollege.getText().toString());
				obj.put("yearClass", editGrade.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(CallNameActivity.this,"正在获取");
				Thread threadLogin = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						// strLoginRst = "success";

						JSONObject jsonRegisterResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.GETPROFESSION);
						Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

						System.out.println("json = "
								+ jsonRegisterResult.toString());
						listProfession.clear();
						try {
							JSONArray jsonArray = jsonRegisterResult
									.getJSONArray("profession");
							for (int i = 0; i < jsonArray.length(); i++) {
								String msg = jsonArray.getString(i);

								listProfession.add(msg);
							}
							Log.i("mytag", "listProfession=========="
									+ listProfession);
							
							Message msg = new Message();
							Bundle b = new Bundle();// 存放数据
							b.putSerializable("listData",
									(Serializable) listProfession);
							msg.setData(b);
							handlerTool.sendMessage(msg);

						} catch (JSONException e) {
							e.printStackTrace();
							handler.sendEmptyMessage(5);
							pdialog.dismiss();
							return;
						}
					}
				});
				threadLogin.start();

			}
		} catch (Exception e) {
			Tools.mToast(CallNameActivity.this, R.string.server_error);
			e.printStackTrace();
			pdialog.dismiss();
		}
	}

	/**
	 * 向服务端发送学院年级专业,获取班级
	 */
	public void getClassData() {

		obj = new JSONObject();
		try {
			if (!editCollege.getText().toString().equals("")
					&& !editGrade.getText().toString().equals("")
					&& !editProfession.getText().toString().equals("")) {
				obj.put("collegeName", editCollege.getText().toString());
				obj.put("yearClass", editGrade.getText().toString());
				obj.put("profession", editProfession.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(CallNameActivity.this,"正在获取");
				Thread threadLogin = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						JSONObject jsonRegisterResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.GETCLASS);
						Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

						System.out.println("json = "
								+ jsonRegisterResult.toString());
						listClass.clear();
						try {
							JSONArray jsonArray = jsonRegisterResult
									.getJSONArray("classID");
							for (int i = 0; i < jsonArray.length(); i++) {
								String msg = jsonArray.getString(i);
								listClass.add(msg);
							}
							Log.i(TAG, "listClass===" + listClass);
							Message msg = new Message();
							Bundle b = new Bundle();// 存放数据
							b.putSerializable("listData",
									(Serializable) listClass);
							msg.setData(b);
							handlerTool.sendMessage(msg);

						} catch (JSONException e) {
							e.printStackTrace();
							handler.sendEmptyMessage(5);
							pdialog.dismiss();
							return;
						}
					}
				});
				threadLogin.start();

			}
		} catch (Exception e) {
			Tools.mToast(CallNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
			pdialog.dismiss();
		}
	}

	/**
	 * 向服务端发送学院年级、专业、班级,获取课程
	 */
	public void getCourseData() {

		obj = new JSONObject();
		try {
			if (!editGrade.getText().toString().equals("")
					&& !editProfession.getText().toString().equals("")
					&& !editClass.getText().toString().equals("")) {
				obj.put("grade", editGrade.getText().toString());
				obj.put("profession", editProfession.getText().toString());
				obj.put("classID", editClass.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(CallNameActivity.this,"正在获取");
				Thread threadLogin = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						JSONObject jsonRegisterResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.GETCOURSE);
						Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

						System.out.println("json = "
								+ jsonRegisterResult.toString());
						listCourse.clear();
						try {
							JSONArray jsonArray = jsonRegisterResult
									.getJSONArray("className");
							for (int i = 0; i < jsonArray.length(); i++) {
								String msg = jsonArray.getString(i);
								listCourse.add(msg);
							}
							Log.i(TAG, "listCourse===" + listCourse);
							Message msg = new Message();
							Bundle b = new Bundle();// 存放数据
							b.putSerializable("listData",
									(Serializable) listCourse);
							msg.setData(b);
							handlerTool.sendMessage(msg);

						} catch (JSONException e) {
							e.printStackTrace();
							handler.sendEmptyMessage(5);
							pdialog.dismiss();
							return;
						}
					}
				});
				threadLogin.start();

			}
		} catch (Exception e) {
			Tools.mToast(CallNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
			pdialog.dismiss();
		}
	}

	/**
	 * 辅导员向服务端发送年级、专业、班级、课程,获取学生所有信息：photo 姓名，学号
	 */
	public void getStudentData() {
		obj = new JSONObject();
		try {
			if (!editGrade.getText().toString().equals("")
					&& !editProfession.getText().toString().equals("")
					&& !editClass.getText().toString().equals("")) {
				obj.put("grade", editGrade.getText().toString());
				obj.put("profession", editProfession.getText().toString());
				obj.put("classID", editClass.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(CallNameActivity.this,"正在获取");
				Thread threadLogin = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						JSONObject jsonAttendResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.GETATTENDENCESTUDENT);
						Log.i("JSON", "jsonRegeditRst" + jsonAttendResult);

						System.out.println("json = "
								+ jsonAttendResult.toString());

						listStrudents = JsonParser
								.parseStudentResult(jsonAttendResult);
						Message msg = new Message();
						Bundle b = new Bundle();// 存放数据
						Log.e(TAG, "listItem======" + listStrudents.size());

						b.putSerializable("listItem",
								(Serializable) listStrudents);
						msg.setData(b);
						handlerSecond.sendMessage(msg);

					}
				});
				threadLogin.start();

			}
		} catch (Exception e) {
			Tools.mToast(CallNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
			pdialog.dismiss();
		}
	}

	/**
	 * 辅导员向服务端发送节数、学号,获取该学生所有信息：photo 姓名，学号
	 */
	public void getOneStudentData() {
		obj = new JSONObject();
		try {
			if (!editLesson.getText().toString().equals("")
					&& !editNum.getText().toString().equals("")) {
				obj.put("classTime", lessonName);
				obj.put("studentNumber", editNum.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(CallNameActivity.this,"正在获取");
				Thread threadUpdate = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						JSONObject jsonAttendResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.ATTENDUPDATE);
						Log.i("JSON", "jsonRegeditRst" + jsonAttendResult);

						listOneStrudents = JsonParser
								.parseOneStudentResult(jsonAttendResult);

						Message msg = new Message();
						Bundle b = new Bundle();// 存放数据
						Log.e(TAG,
								"listOneStrudents======"
										+ listOneStrudents.size());

						b.putSerializable("listItem",
								(Serializable) listOneStrudents);
						msg.setData(b);
						handlerSecond.sendMessage(msg);

					}
				});
				threadUpdate.start();

			}
		} catch (Exception e) {
			Tools.mToast(CallNameActivity.this, "连接服务器失败···");
			e.printStackTrace();
			pdialog.dismiss();
		}
	}

	/**
	 * 辅导员向服务端上传学生数据
	 */
	public void setAttendData() {

		pdialog = Tools.pd(CallNameActivity.this, "正在上传...");
		obj = new JSONObject();
		try {

			obj.put("teacherName", GlobalData.getUsername());
			obj.put("course", editCourse.getText().toString().trim());

			JSONObject jsonObject = JsonParser.packageJsonObject(listStrudents,
					lessonName);

			obj.put("allStudent", jsonObject);

			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadLogin = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonRegisterResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.SENDATTENDENCESHEET);
					Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

					System.out.println("json = "
							+ jsonRegisterResult.toString());
					try {
						result = jsonRegisterResult.getString("Status");
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}

					if (result.equals("Success")) {

						handler.sendEmptyMessage(1);
						pdialog.dismiss();

					} else if (result.equals("Fail")) {
						handler.sendEmptyMessage(2);
						pdialog.dismiss();

					} else {
						handler.sendEmptyMessage(5);
						pdialog.dismiss();
					}
				}
			});
			threadLogin.start();

		} catch (Exception e) {
			Tools.mToast(CallNameActivity.this, "连接服务器失败···");
			pdialog.dismiss();
			e.printStackTrace();
		}
	}

	/**
	 * 辅导员向服务端上传某学生数据
	 */
	public void setOneAttendData() {

		pdialog = Tools.pd(CallNameActivity.this, "正在上传...");
		obj = new JSONObject();
		try {
			obj.put("studentNumber", listOneStrudents.get(0).getNumber());
			obj.put("studyStatus", listOneStrudents.get(0).getStatus());
			obj.put("classTime", lessonName);
			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadSendData = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonResult = urlService.sentParams2DataServer(
							obj, ConstantData.SENDATTENDUPDATE);
					Log.i("JSON", "jsonRegeditRst" + jsonResult);

					System.out.println("json = " + jsonResult.toString());
					try {
						result = jsonResult.getString("Status");
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						pdialog.dismiss();
						return;
					}

					if (result.equals("Success")) {

						handler.sendEmptyMessage(1);
						pdialog.dismiss();

					} else if (result.equals("Fail")) {
						handler.sendEmptyMessage(2);
						pdialog.dismiss();

					} else {
						handler.sendEmptyMessage(5);
						pdialog.dismiss();
					}
				}
			});
			threadSendData.start();

		} catch (Exception e) {
			Tools.mToast(CallNameActivity.this, "连接服务器失败···");
			pdialog.dismiss();
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

	@Override
	public void onItemClick(ActionItem item, int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			flag = "1";
//			getCollegeData();
			showCheckDialog();
			break;
		case 1:
			flag = "2";
			showUpdateDateDialog();
			break;

		}
	}

}
