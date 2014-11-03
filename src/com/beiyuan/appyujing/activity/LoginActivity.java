package com.beiyuan.appyujing.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.beiyuan.appyujing.HeadteacherActivity;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.TeacherActivity;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.EditClick;
import com.beiyuan.appyujing.view.EditClick.OnButtonClickListener;

public class LoginActivity extends MyActivity {

	private TextView tv_register;
	private EditText et_username;
	private EditClick et_password;
	private SharedPreferences preferences;
	private Button bt_login;
	private String username;
	private String password;
	private ProgressDialog pdlogin;
	private JSONObject jsonLoginRst;
	private UrlService urlService = new UrlServiceImpl();
	private Handler handler;

	private JSONObject obj;
	private String strLoginRst;
	private String shenfen;
	private Spinner spinner;
	public String myShenfens[] = { "学生", "普通教师", "辅导员", "领导" };
	private boolean flag = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		init();

		preferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE);

		username = preferences.getString("name", "").trim();
		password = preferences.getString("pswd", "").trim();
		et_username.setText(username);
		et_password.setText(password);

		registerOnClick();
		shenfenCheck();
		loginOnClick();
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					Tools.mToast(LoginActivity.this, R.string.login_success);
					Editor editor = preferences.edit();
					editor.putString("name", username);
					editor.putString("pswd", password);
					editor.putString("shenfen", shenfen);
					editor.putBoolean("login", true);
					editor.commit();

					GlobalData.setUsername(username);
					GlobalData.setRole(shenfen);
					Intent intent = new Intent();
					if (GlobalData.getRole().equals("Role_Student")) {
						intent.setClass(LoginActivity.this,
								StudentActivity.class);
					} else if (GlobalData.getRole().equals("Role_Teacher")) {
						intent.setClass(LoginActivity.this,
								TeacherActivity.class);
					} else {
						intent.setClass(LoginActivity.this,
								HeadteacherActivity.class);
					}
					Log.i("mytag", "------------2");
					startActivity(intent);
					overridePendingTransition(R.anim.push_up_in,
							R.anim.push_up_out);
					finish();

					break;

				case 2:
					Tools.mToast(LoginActivity.this, R.string.login_user_error);
					break;
				case 3:
					Tools.mToast(LoginActivity.this,
							R.string.login_password_error);
					break;
				case 4:
					Tools.mToast(LoginActivity.this,
							R.string.login_user_type_error);
					break;
				case 5:
					Tools.mToast(LoginActivity.this, R.string.server_error);
					break;
				}
			}
		};
	}

	private void shenfenCheck() {
		List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		for (int i = 0; i < myShenfens.length; i++) {

			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("name", myShenfens[i]);

			data.add(map);

		}

		SimpleAdapter adapterClass = new SimpleAdapter(this, data,
				android.R.layout.simple_spinner_item, new String[] { "name" },
				new int[] { android.R.id.text1 });
		adapterClass
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner.setAdapter(adapterClass);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

				if (myShenfens[arg2].equals("领导")) {
					shenfen = "Role_Leader";
				} else if (myShenfens[arg2].equals("普通教师")) {
					shenfen = "Role_Teacher";
				} else if (myShenfens[arg2].equals("辅导员")) {
					shenfen = "Role_HeadTeacher";
				} else {
					shenfen = "Role_Student";
				}

			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	private void loginOnClick() {
		bt_login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				username = et_username.getText().toString().trim();
				password = et_password.getText().toString().trim();

				if (username.equals(null) || username == "") {
					Tools.mToast(LoginActivity.this, R.string.user_empty);
				} else if (password.equals(null) || password == "") {
					Tools.mToast(LoginActivity.this, R.string.password_empty);
				} else if (password.length() < 6) {
					Tools.mToast(LoginActivity.this,
							R.string.password_lenth_error);
				} else {

					pdlogin = Tools.pd(LoginActivity.this);
					obj = new JSONObject();
					try {
						// 从这里将开始连接服务器

						obj.put("role", shenfen);

						obj.put("password", password);
						obj.put("userName", username);
						Log.i("LOGIN", obj.toString());
						// 登陆连接服务器，开辟新的线程
						Thread threadLogin = new Thread(new Runnable() {
							@Override
							public void run() {
								Looper.prepare();
								jsonLoginRst = urlService
										.sentParams2DataServer(obj,
												ConstantData.SENDLOGININFO);

								try {
									strLoginRst = jsonLoginRst
											.getString("Status");
								} catch (JSONException e) {
									e.printStackTrace();
									handler.sendEmptyMessage(5);
									pdlogin.dismiss();
									return;
								}

								if (strLoginRst.equals("Success")) {
									handler.sendEmptyMessage(1);
									pdlogin.dismiss();

								} else if (strLoginRst.equals("NotHaveUser")) {
									handler.sendEmptyMessage(2);
									pdlogin.dismiss();

								} else if (strLoginRst.equals("PasswordError")) {
									handler.sendEmptyMessage(3);
									pdlogin.dismiss();

								} else if (strLoginRst.equals("RoleError")) {
									handler.sendEmptyMessage(4);
									pdlogin.dismiss();

								} else {
									handler.sendEmptyMessage(5);
									pdlogin.dismiss();
								}
							}
						});
						threadLogin.start();

					} catch (Exception e) {

						Tools.mToast(LoginActivity.this, R.string.server_error);
						pdlogin.dismiss();
						e.printStackTrace();
					}
				}

			}
		});

	}

	private void registerOnClick() {
		tv_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
				finish();
			}
		});
	}

	@SuppressLint("NewApi")
	private void init() {
		tv_register = (TextView) findViewById(R.id.login_register);
		et_username = (EditText) findViewById(R.id.login_username);
		et_password = (EditClick) findViewById(R.id.login_password);
		et_password.setHint(R.string.password);
		et_password.setPassword(false);

		Drawable eye_drawable = getResources().getDrawable(R.drawable.eye_btn);
		Drawable text_drawable = getResources().getDrawable(
				R.drawable.login_password_style);
		Drawable name_left_drawable = getResources().getDrawable(
				R.drawable.user_name_img);
		et_password.setBtnBackground(eye_drawable);
		et_password.setEditBackground(text_drawable);
		et_password.setEditDrawable(name_left_drawable);
		bt_login = (Button) findViewById(R.id.login_bt_login);
		spinner = (Spinner) findViewById(R.id.login_shenfen);
		et_password.setRightButton(new OnButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				et_password.setPassword(flag);
				flag = !flag;
			}

		});
	}

}