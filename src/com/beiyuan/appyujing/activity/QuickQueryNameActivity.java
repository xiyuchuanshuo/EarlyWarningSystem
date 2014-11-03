package com.beiyuan.appyujing.activity;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;

/**
 * 快速查询：领导：获取学院，辅导员：获取班级
 * 
 * @author juan
 * 
 */
public class QuickQueryNameActivity extends MyActivity {

	private TitleView mTitle;
	private ListView quickquery_list;
	private UrlService urlservice = new UrlServiceImpl();
	private ArrayList<String> listCollege = new ArrayList<String>();
	private ArrayList<String> listGrade = new ArrayList<String>();
	private ArrayList<String> listGradeId = new ArrayList<String>();
	private final static String TAG = "QuickQueryNameActivity";
	private Handler handler;
	private ArrayAdapter<String> adapterCollege;
	private JSONObject obj;
	private UrlService urlService = new UrlServiceImpl(); 
    private ProgressDialog pdquery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_name);
		initView();
		getListener();
		if (GlobalData.getRole().equals("Role_Leader")) {

			getCollegeData();
		} else {
			getClassByTeacher();
		}

		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					adapterCollege = new ArrayAdapter<String>(
							QuickQueryNameActivity.this,
							android.R.layout.simple_expandable_list_item_1,
							listCollege);
					quickquery_list.setAdapter(adapterCollege);
					pdquery.dismiss();
					break;
				case 2:
					adapterCollege = new ArrayAdapter<String>(
							QuickQueryNameActivity.this,
							android.R.layout.simple_expandable_list_item_1,
							listGrade);
					quickquery_list.setAdapter(adapterCollege);
					pdquery.dismiss();
					break;
				case 5:
					Tools.mToast(QuickQueryNameActivity.this, R.string.server_error);
					pdquery.dismiss();
					break;
				}
			}
		};
	}

	private void getListener() {
		// TODO Auto-generated method stub
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}

		});

		quickquery_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				if (GlobalData.getRole().equals("Role_Leader")) {
					TextView text = (TextView) view
							.findViewById(android.R.id.text1);
					String collegeName = text.getText().toString();
					Log.i(TAG, "collegeName======" + collegeName);
					intent.setClass(QuickQueryNameActivity.this,
							QuickQueryStudentActivity.class);
					intent.putExtra("collegename", collegeName);
				} else {
					intent.setClass(QuickQueryNameActivity.this,
							QuickQueryStudentActivity.class);
					intent.putExtra("collegename", listGradeId.get(position));
				}
				QuickQueryNameActivity.this.startActivity(intent);

			}

		});

	}

	public void initView() {
		
		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.query_name_title);
		quickquery_list = (ListView) findViewById(R.id.query_name_list);
	}

	/**
	 * 获取学院、年级数据
	 */
	public void getCollegeData() {
		// 开线程，从服务端获取学院、年级数据
		pdquery = Tools.pd(QuickQueryNameActivity.this, "正在获取");
		Thread threadRegister = new Thread(new Runnable() {

			@Override
			public void run() {
				String jsonToString = urlService
						.getJsonContent(ConstantData.GETCOLLEGEGRA);
				listCollege = (ArrayList<String>) urlService.getCollegeList(
						"college", jsonToString);
				Log.i(TAG, "listString===" + listCollege);
				handler.sendEmptyMessage(1);
			}
		});
		threadRegister.start();
	}

	/**
	 * 辅导员向服务端发送辅导员昵称,获取班级
	 */
	public void getClassByTeacher() {

		obj = new JSONObject();
		try {
			pdquery = Tools.pd(QuickQueryNameActivity.this,"正在获取");
			obj.put("headTeacherNickname", GlobalData.getUsername().toString()
					.trim());

			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadQuick = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonGradeResult = urlservice
							.sentParams2DataServer(obj,
									ConstantData.EMEGETCLASSBYTEACHER);
					Log.i("JSON", "jsonRegeditRst" + jsonGradeResult);

					System.out.println("json = " + jsonGradeResult.toString());
					listGrade.clear();
					listGradeId.clear();
					try {
						String gradeByTeacher = jsonGradeResult
								.getString("grade");
						JSONObject jsonGrade = new JSONObject(gradeByTeacher);
						Log.i("mytag", "jsonGrade====" + jsonGrade.toString());

						for (int i = 0; i < jsonGrade.length(); i++) {
							String gradearray = jsonGrade.getString("" + i);
							JSONObject jsongrade = new JSONObject(gradearray);
							String gradeId = jsongrade.getString("gradeID");
							String grade = jsongrade.getString("grade");
							String profession = jsongrade
									.getString("profession");
							String classId = jsongrade.getString("classID");
							String name = grade + profession + classId + "班";

							listGrade.add(name);
							listGradeId.add(gradeId + "");
							handler.sendEmptyMessage(2);
						}
						Log.i(TAG, "listGrade==========" + listGrade);
						Log.i(TAG, "listGradeId==========" + listGradeId);

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}
			});
			threadQuick.start();

		} catch (Exception e) {
			Tools.mToast(QuickQueryNameActivity.this, R.string.server_error);
			pdquery.dismiss();
			e.printStackTrace();
		}
	}

}
