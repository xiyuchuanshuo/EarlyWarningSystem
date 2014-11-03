package com.beiyuan.appyujing.activity;

import java.io.Serializable;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.EditClick;
import com.beiyuan.appyujing.view.EditClick.OnButtonClickListener;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;

/**
 * 教师、普通教师注册
 * 
 * @author juan
 * 
 */
public class RegisterTeaFinishActivity extends MyActivity {

	private TitleView mTitle;
	private Bundle bundle;

	private String collegeName;
	private EditText name, nickName;
	private EditText phone;
	private EditText idCard;
	private EditClick password;
	private LinearLayout collegelayout;
	private LinearLayout gradelayout;
	private Dialog builder = null;
	private String flagname;
	private UrlService urlService = new UrlServiceImpl();
	private Handler handler;
	private String strRegisterResult;
	private JSONObject obj;
	private EditClick teacherCollege;
	private EditClick teacherGrade;

	private ArrayList<String> listItem = new ArrayList<String>();
	private ArrayList<String> listCollege = new ArrayList<String>();
	private ArrayList<String> listGrade = new ArrayList<String>();
	private ArrayList<String> listGradeId = new ArrayList<String>();
	private ArrayList<String> listsendId = new ArrayList<String>();

	private final static String TAG = "RegisterTeacherFinish";
	private StringBuffer str = new StringBuffer();
	private ProgressDialog pdRegister;
	private String tp = null;
	private boolean flag = true;
	private Bundle bundleb;
	ArrayAdapter<String> adapterCollege;
	ListView listView_college;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_finish_teacher);
		// 判断身份
		bundle = getIntent().getExtras();
		flagname = bundle.getString("flag");
		Log.e("mytag", "flagname=====" + flagname);

		if (flagname.equals("headteacher")) {
			initViewHeadTeacher();
		} else {
			initViewTeacher();
		}

		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.register_success);
					Intent intent = new Intent(RegisterTeaFinishActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
					break;
				case 2:
					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.register_fail);
					break;
				case 3:
					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.register_nickname_error);
					break;
				case 4:
					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.register_username_error);
					break;
				case 5:
					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.server_error);
					break;
				case 6:
					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.register_no_user);
					break;
				case 7:
					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.register_no_server);
					break;
				}
			}
		};

	}

	/**
	 * 刷新
	 */
	private Handler handlerSecond = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			bundleb = msg.getData();
			listItem = (ArrayList<String>) bundleb.get("listItem");
			Log.i(TAG, "listItem2===" + listItem.size());
			adapterCollege = new ArrayAdapter<String>(
					RegisterTeaFinishActivity.this,
					android.R.layout.simple_expandable_list_item_1, listItem);
			listView_college.setAdapter(adapterCollege);
			pdRegister.dismiss();
		}
	};

	@SuppressLint("NewApi")
	public void initView() {

		collegelayout = (LinearLayout) findViewById(R.id.teacher_college_layout);
		gradelayout = (LinearLayout) findViewById(R.id.teacher_grade_layout);
		name = (EditText) findViewById(R.id.teacher_username);
		nickName = (EditText) findViewById(R.id.teacher_nickname);
		phone = (EditText) findViewById(R.id.teacher_usernum);
		idCard = (EditText) findViewById(R.id.teacher_useridcard);
		password = (EditClick) findViewById(R.id.teacher_password);
		password.setHint(R.string.password);
		password.setPassword(false);
		Drawable drawable = getResources().getDrawable(R.drawable.eye_btn);
		password.setBtnBackground(drawable);
		password.setHint(R.string.register_user_password);

	}

	/**
	 * 辅导员界面
	 */
	private void initViewHeadTeacher() {
		initView();

		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.register_title);

		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(
				R.drawable.map_upload_img);

		mTitle.setLeftButton(leftImg, new TeacherLeftListener());
		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				sentHttpHeadTeacherData();
			}
		});
		collegelayout.setVisibility(0);
		gradelayout.setVisibility(0);

		teacherCollege = (EditClick) findViewById(R.id.register_teacher_college);
		teacherGrade = (EditClick) findViewById(R.id.register_teacher_grade);
		teacherCollege.setHint(R.string.register_check_college);
		teacherGrade.setHint(R.string.register_check_class);

		teacherCollege.setEditable(false);
		teacherGrade.setEditable(false);
		password.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				password.setPassword(flag);
				flag = !flag;
			}

		});
		teacherCollege.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showCollegeDialog();
			}
		});
		teacherGrade.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				showTeacherGradeDialog();
			}
		});

	}

	/**
	 * 普通教师界面
	 */
	private void initViewTeacher() {
		initView();

		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.register_title);

		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(
				R.drawable.map_upload_img);
		mTitle.setLeftButton(leftImg, new TeacherLeftListener());
		mTitle.setRightButton(rightImg, new TeacherRightListener());
		password.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				password.setPassword(flag);
				flag = !flag;
			}

		});
	}

	// 所有左键返回
	private class TeacherLeftListener implements OnLeftButtonClickListener {

		@Override
		public void onClick(View button) {
			// TODO Auto-generated method stub
			RegisterTeaFinishActivity.this.finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		}

	}

	// 辅导员右键完成,所有数据上传到服务器
	@SuppressWarnings("unused")
	private class HeadTeacherRightListener implements
			OnRightButtonClickListener {
		@Override
		public void onClick(View button) {
			// TODO Auto-generated method stub
			sentHttpHeadTeacherData();
		}

	}

	// 普通教师右键完成,所有数据上传到服务器
	private class TeacherRightListener implements OnRightButtonClickListener {

		@Override
		public void onClick(View button) {
			// TODO Auto-generated method stub
			sentHttpTeacherData();
		}

	}

	/**
	 * 教师选择学院
	 */
	@SuppressLint("NewApi")
	public void showCollegeDialog() {

		builder = new Dialog(this);
		builder.setTitle(R.string.register_check_college);
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

//		listView_college.setAdapter(adapterCollege);
		getCollegeData();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				collegeName = text.getText().toString();
				Log.i("mytag", "collegename======" + collegeName);
				getProfessionData(collegeName);
				teacherCollege.setText(collegeName);
				builder.dismiss();
			}

		});

	}

	// 教师年级多选对话框
	public void showTeacherGradeDialog() {

		final String[] mItems = new String[listGrade.size()];
		for (int i = 0; i < listGrade.size(); i++) {
			mItems[i] = listGrade.get(i);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(
				RegisterTeaFinishActivity.this);

		listsendId.clear();
		builder.setTitle(R.string.register_check_grade);
		final boolean[] a = new boolean[listGrade.size()];
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
		builder.setPositiveButton(R.string.emerge_sure,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						StringBuilder stringBuilder = new StringBuilder();
						for (int i = 0; i < a.length; i++) {
							if (a[i] == true) {
								stringBuilder.append(mItems[i] + "、");
								listsendId.add(i + "");
							}
						}
						teacherGrade.setText(stringBuilder.toString());
					}
				});
		builder.setNegativeButton(R.string.emerge_cancle,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				});
		builder.create().show();
	}

	public void getCollegeData() {
		// 开线程，从服务端获取学院、年级数据
		pdRegister = Tools.pd(RegisterTeaFinishActivity.this, "正在获取");
		Thread threadRegister = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					String jsonToString = urlService
							.getJsonContent(ConstantData.GETTEACHERCOLLEGE);
					listCollege = (ArrayList<String>) urlService
							.getCollegeList("college", jsonToString);
					Log.i(TAG, "listString===" + listCollege);
					Message msg = new Message();
					Bundle b = new Bundle();// 存放数据
					Log.e(TAG, "listCollege======" + listCollege.size());
					b.putSerializable("listItem", (Serializable) listCollege);
					msg.setData(b);
					handlerSecond.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(5);
					pdRegister.dismiss();
					return;
				}

			}
		});
		threadRegister.start();
	}

	/**
	 * 辅导员向服务端发送学院,获取所有信息
	 */
	public void getProfessionData(String name) {

		obj = new JSONObject();
		try {
			if (name.equals("")) {
				return;
			}
			obj.put("collegeName", name);

			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadLogin = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonRegisterResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.GETTEACHERGRADE);
					Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

					System.out.println("json = "
							+ jsonRegisterResult.toString());
					try {
						JSONArray jsonArray = jsonRegisterResult
								.getJSONArray("grade");
						Log.i("mytag", "jsonArray====" + jsonArray.toString());
						for (int i = 0; i < jsonArray.length(); i++) {
							String gradeId = jsonArray.getJSONObject(i)
									.getString("gradeId");
							String profession = jsonArray.getJSONObject(i)
									.getString("profession");
							String yearClass = jsonArray.getJSONObject(i)
									.getString("yearClass");
							String classId = jsonArray.getJSONObject(i)
									.getString("classId");
							String name = yearClass + profession + classId
									+ "班";
							listGrade.add(name);
							listGradeId.add(gradeId + "");
						}
						Log.i(TAG, "listGrade==========" + listGrade);
						Log.i(TAG, "listGradeId==========" + listGradeId);

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						pdRegister.dismiss();
						return;
					}
				}
			});
			threadLogin.start();

		} catch (Exception e) {
			Tools.mToast(RegisterTeaFinishActivity.this, R.string.server_error);
			pdRegister.dismiss();
			e.printStackTrace();
		}
	}

	/**
	 * 辅导员向服务器中上传数据
	 */
	public void sentHttpHeadTeacherData() {

		String tnameText = name.getText().toString().trim();
		String tnicknameText = nickName.getText().toString().trim();
		String tnumText = phone.getText().toString().trim();
		String tidCardText = idCard.getText().toString().trim();

		String tpasswordText = password.getText().toString().trim();

		Log.i(TAG, "tnameText=" + tnameText + ";tnicknameText=" + tnicknameText
				+ ";tnumText=" + tnumText + ";tidCardText=" + tidCardText
				+ ";tpasswordText=" + tpasswordText + ";");

		if ("".equals(tnameText)) {
			Tools.mToast(getApplication(), R.string.register_username_empty);
			return;
		}

		if ("".equals(tnicknameText)) {
			Tools.mToast(getApplication(), R.string.register_nickname_empty);
			return;
		}

		if ("".equals(tnumText)) {
			Tools.mToast(getApplication(), R.string.register_phone_empty);
			return;
		}

		if ("".equals(tidCardText)) {
			Tools.mToast(getApplication(), R.string.register_cardid_empty);
			return;

		}
		if (tpasswordText.length() < 7 || tpasswordText.length() > 16) {
			Tools.mToast(getApplication(), R.string.register_password_rules);
			password.setText("");
			return;
		}
		if (teacherCollege.getText().toString().equals("")) {
			Tools.mToast(getApplication(), R.string.emerge_colleage_empty);
			return;
		}

		if (listsendId.size() == 0) {
			Tools.mToast(getApplication(), R.string.emerge_grade_empty);
			return;
		}

		// 向服务端提交数据
		pdRegister = Tools.pd(RegisterTeaFinishActivity.this,
				R.string.registering);
		obj = new JSONObject();
		JSONArray objk = new JSONArray();
		try {
			obj.put("role", "Role_HeadTeacher");
			obj.put("headTeacherName", tnameText);
			obj.put("nickname", tnicknameText);
			obj.put("teacherPhone", tnumText);
			obj.put("teacherCardId", tidCardText);
			obj.put("password", tpasswordText);
			obj.put("collegeName", teacherCollege.getText().toString());

			Log.i(TAG, "setlist====" + listsendId);
			Log.i(TAG, "obj====" + obj.toString());

			for (int i = 0; i < listsendId.size(); i++) {
				objk.put(Integer.parseInt(listGradeId.get(Integer
						.parseInt(listsendId.get(i)))));
				Log.i("mytag",
						listGradeId.get(Integer.parseInt(listsendId.get(i))));
			}
			obj.put("grade", objk);

			Log.i(TAG, obj.toString());

			Thread threadLogin = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonRegisterResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.REGISTERHEADTEACHER);
					Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

					System.out.println("json = "
							+ jsonRegisterResult.toString());
					try {
						strRegisterResult = jsonRegisterResult
								.getString("Status");
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						pdRegister.dismiss();
						return;
					}

					if (strRegisterResult.equals("Success")) {
						handler.sendEmptyMessage(1);
						pdRegister.dismiss();

					} else if (strRegisterResult.equals("UserNameRepeat")) {
						handler.sendEmptyMessage(3);
						pdRegister.dismiss();

					} else if (strRegisterResult.equals("Fail")) {
						handler.sendEmptyMessage(2);
						pdRegister.dismiss();

					} else if (strRegisterResult.equals("")) {
						handler.sendEmptyMessage(7);
						pdRegister.dismiss();

					} else {
						handler.sendEmptyMessage(5);
						pdRegister.dismiss();
					}
				}
			});
			threadLogin.start();

		} catch (Exception e) {
			Tools.mToast(RegisterTeaFinishActivity.this, R.string.server_error);
			pdRegister.dismiss();
			e.printStackTrace();
		}
	}

	/**
	 * 普通教师向服务器中上传数据
	 */
	public void sentHttpTeacherData() {
		String tnameText = name.getText().toString().trim();
		String tnicknameText = nickName.getText().toString().trim();
		String tnumText = phone.getText().toString().trim();
		String tidCardText = idCard.getText().toString().trim();

		String tpasswordText = password.getText().toString().trim();

		if ("".equals(tnameText)) {
			Tools.mToast(getApplication(), R.string.register_username_empty);
		}

		if ("".equals(tnicknameText)) {
			Tools.mToast(getApplication(), R.string.register_nickname_empty);
		}

		if ("".equals(tnumText)) {
			Tools.mToast(getApplication(), R.string.register_phone_empty);
		}

		if ("".equals(tidCardText)) {
			Tools.mToast(getApplication(), R.string.register_cardid_empty);

		}
		if (tpasswordText.length() < 7 || tpasswordText.length() > 16) {
			Tools.mToast(getApplication(), R.string.register_password_rules);
			password.setText("");
		}

		if ((!"".equals(tnameText)) && (!"".equals(tnicknameText))
				&& (!"".equals(tnumText)) && (!"".equals(tidCardText))
				&& (!"".equals(tpasswordText))) {
			if ((tpasswordText.length() < 17) && (tpasswordText.length() > 6)) {
				// 向服务端提交数据
				pdRegister = Tools.pd(RegisterTeaFinishActivity.this,
						R.string.registering);
				obj = new JSONObject();
				try {
					obj.put("role", "Role_Teacher");
					obj.put("teacherName", tnameText);
					obj.put("nickname", tnicknameText);
					obj.put("teacherPhone", tnumText);
					obj.put("teacherCardId", tidCardText);
					obj.put("password", tpasswordText);

					Log.i("mytag", obj.toString());

					Thread threadLogin = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();

							JSONObject jsonRegisterResult = urlService
									.sentParams2DataServer(obj,
											ConstantData.REGISTERTEACHER);
							Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

							try {
								strRegisterResult = jsonRegisterResult
										.getString("Status");
							} catch (JSONException e) {
								e.printStackTrace();
								handler.sendEmptyMessage(5);
								pdRegister.dismiss();
								return;
							}

							if (strRegisterResult.equals("Success")) {

								handler.sendEmptyMessage(1);
								pdRegister.dismiss();

							} else if (strRegisterResult
									.equals("UserNicknameRepeat")) {
								handler.sendEmptyMessage(3);
								pdRegister.dismiss();

							} else if (strRegisterResult.equals("Fail")) {
								handler.sendEmptyMessage(2);
								pdRegister.dismiss();

							} else if (strRegisterResult
									.equals("UserNameError")) {
								handler.sendEmptyMessage(4);
								pdRegister.dismiss();
							} else if (strRegisterResult.equals("NotHaveUser")) {
								handler.sendEmptyMessage(6);
								pdRegister.dismiss();
							} else if (strRegisterResult.equals("")) {
								handler.sendEmptyMessage(7);
								pdRegister.dismiss();
							} else {
								handler.sendEmptyMessage(5);
								pdRegister.dismiss();
							}
						}
					});
					threadLogin.start();

				} catch (Exception e) {

					Tools.mToast(RegisterTeaFinishActivity.this,
							R.string.server_error);
					pdRegister.dismiss();
					e.printStackTrace();
				}
			}
		}
	}
}
