package com.beiyuan.appyujing.activity;

import java.io.Serializable;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
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
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.EditClick;
import com.beiyuan.appyujing.view.EditClick.OnButtonClickListener;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;

public class RegisterStuFinishActivity extends MyActivity {

	private TitleView mTitle;
	private EditClick mEditClicka;
	private EditClick mEditClickb;
	private EditClick mEditClickc;
	private EditClick mEditClickd;

	private EditText name, nickName;
	private EditText studentId, phone;
	private EditText idCard;
	private EditClick password;

	private Dialog builder = null;
	private ProgressDialog pdRegister;
	private ProgressDialog pdialog;
	private UrlService urlService = new UrlServiceImpl();
	private Handler handler;
	private String strRegisterResult;
	private JSONObject obj;
	private String tp = null;
	private ArrayList<String> listItem = new ArrayList<String>();
	private ArrayList<String> listCollege = new ArrayList<String>();
	private ArrayList<String> listGrade = new ArrayList<String>();
	private ArrayList<String> listProfession = new ArrayList<String>();
	private ArrayList<String> listClass = new ArrayList<String>();

	private Bundle extras;
	private boolean flag = true;
	private Bundle bundleb;
	ListView listView_college;
	ArrayAdapter<String> adapterCollege;

	private final static String TAG = "RegisterFinish";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_finish_student);
		initView();
		getListener();
		// 判断身份

		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					Tools.mToast(RegisterStuFinishActivity.this,
							R.string.register_success);
					Intent intent = new Intent(RegisterStuFinishActivity.this,
							LoginActivity.class);
					startActivity(intent);
					finish();
					break;
				case 2:
					Tools.mToast(RegisterStuFinishActivity.this,
							R.string.register_fail);
					break;
				case 3:
					Tools.mToast(RegisterStuFinishActivity.this,
							R.string.register_nickname_error);
					break;
				case 4:
					Tools.mToast(RegisterStuFinishActivity.this,
							R.string.register_username_error);
					break;
				case 5:
					Tools.mToast(RegisterStuFinishActivity.this,
							R.string.server_error);
					break;
				case 6:
					Tools.mToast(RegisterStuFinishActivity.this,
							R.string.register_no_user);
					break;
				case 7:
					Tools.mToast(RegisterStuFinishActivity.this,
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
					RegisterStuFinishActivity.this,
					android.R.layout.simple_expandable_list_item_1, listItem);
			listView_college.setAdapter(adapterCollege);
			pdialog.dismiss();
		}
	};

	/**
	 * 学生界面
	 */
	@SuppressLint("NewApi")
	private void initView() {

		// initDate();
		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.register_title);
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(
				R.drawable.map_upload_img);

		mTitle.setLeftButton(leftImg, new StudentLeftListener());
		mTitle.setRightButton(rightImg, new StudentRightListener());

		name = (EditText) findViewById(R.id.student_username);
		nickName = (EditText) findViewById(R.id.student_usernickname);
		studentId = (EditText) findViewById(R.id.student_userid);
		phone = (EditText) findViewById(R.id.student_usernum);
		idCard = (EditText) findViewById(R.id.student_useridcard);

		password = (EditClick) findViewById(R.id.student_password);
		password.setHint(R.string.password);
		password.setPassword(false);
		Drawable drawable = getResources().getDrawable(R.drawable.eye_btn);
		password.setBtnBackground(drawable);
		password.setHint(R.string.register_user_password);
		mEditClicka = (EditClick) findViewById(R.id.register_college);
		mEditClickb = (EditClick) findViewById(R.id.register_grade);
		mEditClickc = (EditClick) findViewById(R.id.register_profession);
		mEditClickd = (EditClick) findViewById(R.id.register_class);

		mEditClicka.setEditable(false);
		mEditClickb.setEditable(false);
		mEditClickc.setEditable(false);
		mEditClickd.setEditable(false);

		mEditClicka.setHint(R.string.register_check_college);
		mEditClickb.setHint(R.string.register_check_grade);
		mEditClickc.setHint(R.string.register_check_profession);
		mEditClickd.setHint(R.string.register_check_class);

	}

	public void getListener() {
		password.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				password.setPassword(flag);
				flag = !flag;
			}

		});
		mEditClicka.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showCollegeDialog();
			}
		});
		mEditClickb.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showGradeDialog();

			}
		});
		mEditClickc.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showProfessionDialog();
			}
		});
		mEditClickd.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				showClassDialog();
			}
		});
	}


	/**
	 * 学生选择学院
	 */
	@SuppressLint("NewApi")
	public void showCollegeDialog() {
		Log.i("mytag", "-------------a");
		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_colleage_check);
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
//		adapterCollege = new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, listCollege);
		getColGraDate();
//		listView_college.setAdapter(adapterCollege);
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String collegename = text.getText().toString();
				mEditClicka.setText(collegename);
				builder.dismiss();
			}

		});
	}

	/**
	 * 学生选择年级
	 */
	@SuppressLint("NewApi")
	public void showGradeDialog() {

		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_grade_check);
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
				String gradename = text.getText().toString();
				mEditClickb.setText(gradename);
				builder.dismiss();
				// getProfessionData();
			}

		});
		adapterGrade.notifyDataSetChanged();
	}

	/**
	 * 学生选择专业
	 */
	@SuppressLint("NewApi")
	public void showProfessionDialog() {
		if (mEditClicka.getText().toString().equals("")) {
			Tools.mToast(RegisterStuFinishActivity.this, "学院不能为空");
			return;
		}
		if (mEditClickb.getText().toString().equals("")) {
			Tools.mToast(RegisterStuFinishActivity.this, "年级不能为空");
			return;
		}
		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_profession_check);
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

//		adapterCollege = new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, listProfession);
		getProfessionData();
//		listView_college.setAdapter(adapterCollege);

		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String professionname = text.getText().toString();
				mEditClickc.setText(professionname);
				builder.dismiss();
				// getClassData();
			}

		});
//		adapterCollege.notifyDataSetChanged();
	}

	/**
	 * 学生选择班级
	 */
	@SuppressLint("NewApi")
	public void showClassDialog() {
		
		if (mEditClicka.getText().toString().equals("")) {
			Tools.mToast(RegisterStuFinishActivity.this, "学院不能为空");
			return;
		}
		if (mEditClickb.getText().toString().equals("")) {
			Tools.mToast(RegisterStuFinishActivity.this, "年级不能为空");
			return;
		}
		if (mEditClickc.getText().toString().equals("")) {
			Tools.mToast(RegisterStuFinishActivity.this, "专业不能为空");
			return;
		}
		builder = new Dialog(this);
		builder.setTitle(R.string.emerge_class_check);
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

//		adapterCollege = new ArrayAdapter<String>(this,
//				android.R.layout.simple_expandable_list_item_1, listClass);
//		listView_college.setAdapter(adapterCollege);

		getClassData();
		listView_college.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView text = (TextView) view
						.findViewById(android.R.id.text1);
				String classname = text.getText().toString();
				mEditClickd.setText(classname);
				builder.dismiss();

			}

		});
//		adapterCollege.notifyDataSetChanged();
	}

	// 所有左键返回
	private class StudentLeftListener implements OnLeftButtonClickListener {

		@Override
		public void onClick(View button) {
			// TODO Auto-generated method stub
			RegisterStuFinishActivity.this.finish();
			overridePendingTransition(R.anim.push_right_in,
					R.anim.push_right_out);
		}
	}

	// 学生右键完成,所有数据上传到服务器
	private class StudentRightListener implements OnRightButtonClickListener {

		@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
		@Override
		public void onClick(View button) {
			// TODO Auto-generated method stub

			String snameText = name.getText().toString().trim();
			String snicknameText = nickName.getText().toString().trim();
			String sidText = studentId.getText().toString().trim();
			String snumText = phone.getText().toString().trim();
			String sidCardText = idCard.getText().toString().trim();
			String spasswordText = password.getText().toString().trim();

			if ("".equals(snameText)) {
				Tools.mToast(getApplication(), R.string.register_username_empty);
				return;
			}

			if ("".equals(snicknameText)) {
				Tools.mToast(getApplication(), R.string.register_nickname_empty);
				return;
			}

			if ("".equals(sidText)) {
				Tools.mToast(getApplication(), R.string.register_usernum_empty);
				return;

			}

			if ("".equals(snumText)) {
				Tools.mToast(getApplication(), R.string.register_phone_empty);
				return;
			}

			if ("".equals(sidCardText)) {
				Tools.mToast(getApplication(), R.string.register_cardid_empty);
				return;

			}
			if (spasswordText.length() < 7 || spasswordText.length() > 16) {
				Tools.mToast(getApplication(), R.string.register_password_rules);
				password.setText("");
				return;
			}
			if (mEditClicka.getText().toString().equals("")) {
				Tools.mToast(getApplication(), R.string.emerge_colleage_empty);
				return;
			}
			if (mEditClickb.getText().toString().equals("")) {
				Tools.mToast(getApplication(), R.string.emerge_grade_empty);
				return;
			}
			if (mEditClickc.getText().toString().equals("")) {
				Tools.mToast(getApplication(), R.string.emerge_profession_empty);
				return;

			}
			if (mEditClickd.getText().toString().equals("")) {
				Tools.mToast(getApplication(), R.string.emerge_class_empty);
				return;
			}

			if ((spasswordText.length() < 17) && (spasswordText.length() > 6)) {
				// 向服务端提交数据
				pdRegister = Tools.pd(RegisterStuFinishActivity.this,
						R.string.registering);
				obj = new JSONObject();
				try {
					obj.put("role", "Role_Student");
					obj.put("studentName", snameText);
					obj.put("nickname", snicknameText);
					obj.put("studentPhone", snumText);
					obj.put("id", sidText);
					obj.put("studentCardId", sidCardText);
					obj.put("password", spasswordText);
					obj.put("college", mEditClicka.getText().toString());
					obj.put("grade", mEditClickb.getText().toString());
					obj.put("profession", mEditClickc.getText().toString());
					obj.put("classId", mEditClickd.getText().toString());

					Log.i(TAG, "obj====" + obj.toString());

					Thread threadRegister = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							Looper.prepare();

							JSONObject jsonRegisterResult = urlService
									.sentParams2DataServer(obj,
											ConstantData.REGISTERSTUDENT);

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
									.equals("UserNameRepeat")) {
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
					threadRegister.start();

				} catch (Exception e) {

					Tools.mToast(RegisterStuFinishActivity.this,
							R.string.server_error);
					pdRegister.dismiss();
					e.printStackTrace();
				}
			}
		}
	}
	public void getColGraDate() {
		// 开线程，从服务端获取学院、年级数据
		pdialog = Tools.pd(RegisterStuFinishActivity.this, "正在获取");
		Thread threadRegister = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				try {
					
					String jsonToString = urlService
							.getJsonContent(ConstantData.GETCOLLEGEGRA);
					listCollege = (ArrayList<String>) urlService
							.getCollegeList("college", jsonToString);
					listGrade = (ArrayList<String>) urlService.getCollegeList(
							"grade", jsonToString);

					Message msg = new Message();
					Bundle b = new Bundle();// 存放数据
					Log.e(TAG, "listCollege======" + listCollege.size());
					b.putSerializable("listItem", (Serializable) listCollege);
					msg.setData(b);
					handlerSecond.sendMessage(msg);

				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(5);
					pdialog.dismiss();
					return;
				}

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
			
			if (!mEditClicka.getText().toString().equals("")
					&& !mEditClickb.getText().toString().equals("")) {
				obj.put("collegeName", mEditClicka.getText().toString());
				obj.put("yearClass", mEditClickb.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(RegisterStuFinishActivity.this,"正在获取");
				Thread threadLogin = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						JSONObject jsonRegisterResult = urlService
								.sentParams2DataServer(obj,
										ConstantData.GETPROFESSION);
						Log.i("JSON", "jsonRegeditRst" + jsonRegisterResult);

						listProfession.clear();
						try {
							JSONArray jsonArray = jsonRegisterResult
									.getJSONArray("profession");
							for (int i = 0; i < jsonArray.length(); i++) {
								String msg = jsonArray.getString(i);

								listProfession.add(msg);
							}
							Message msg = new Message();
							Bundle b = new Bundle();// 存放数据
							Log.e(TAG,
									"listProfession======"
											+ listProfession.size());

							b.putSerializable("listItem",
									(Serializable) listProfession);
							msg.setData(b);
							handlerSecond.sendMessage(msg);

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

			Tools.mToast(RegisterStuFinishActivity.this, R.string.server_error);
			pdialog.dismiss();
			e.printStackTrace();
		}
	}

	/**
	 * 向服务端发送学院年级专业,获取班级
	 */
	public void getClassData() {

		obj = new JSONObject();
		try {
			
			if (!mEditClicka.getText().toString().equals("")
					&& !mEditClickb.getText().toString().equals("")
					&& !mEditClickc.getText().toString().equals("")) {
				obj.put("collegeName", mEditClicka.getText().toString());
				obj.put("yearClass", mEditClickb.getText().toString());
				obj.put("profession", mEditClickc.getText().toString());

				Log.i(TAG, "obj=====" + obj.toString());
				pdialog = Tools.pd(RegisterStuFinishActivity.this, "正在获取");
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
							b.putSerializable("listItem",
									(Serializable) listClass);
							msg.setData(b);
							handlerSecond.sendMessage(msg);

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

			Tools.mToast(RegisterStuFinishActivity.this, R.string.server_error);
			e.printStackTrace();
			pdialog.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// Do something.
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
