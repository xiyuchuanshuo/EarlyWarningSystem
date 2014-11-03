package com.beiyuan.appyujing.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.beiyuan.appyujing.HeadteacherActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.TeacherActivity;
import com.beiyuan.appyujing.activity.EmerConCheckActivity;
import com.beiyuan.appyujing.activity.EmergeContactActivity;
import com.beiyuan.appyujing.activity.RegisterTeaFinishActivity;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.database.table.ClassMates;
import com.beiyuan.appyujing.database.table.HeadTeacher;
import com.beiyuan.appyujing.database.table.Leader;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.DragLayout;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;
import com.iflytek.cloud.ui.RecognizerDialog;

/**
 * 紧急情况模块 ，分为三个角色：领导，辅导员，学生。
 * 
 * @author juan 领导的功能：查看领导，查询某学院的辅导员，查询某班级的学生 辅导员的功能：查看领导和该学院的辅导员，查询所管理班级
 *         学生的功能：查看领导、本班级的辅导员、本班级同学
 */
public class EmergencyFragment extends Fragment {

	private Handler handler;
	private JSONObject obj;
	private UrlService urlService = new UrlServiceImpl();
	private String strEmergencyResult;
	private final static String TAG = "EmergencyFragment";
	private EmergencyHelper emergencyHelper;
	private TitleView mTitle;
	private RecognizerDialog dialog;
	private ProgressDialog emerge_dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View parentView = inflater
				.inflate(R.layout.fragment2, container, false);
		initView(parentView);
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					Tools.mToast(getActivity(), R.string.emerge_get_success);
					break;
				case 2:
					Tools.mToast(getActivity(), R.string.emerge_get_fail);
					break;
				case 5:
					Tools.mToast(getActivity(), R.string.server_error);
					break;
				}
			}
		};

		return parentView;
	}
	private DragLayout dl;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mTitle = (TitleView) getView().findViewById(R.id.title);
		mTitle.setTitle(R.string.emerge_title);
		Drawable leftImg = getResources().getDrawable(R.drawable.side_btn_img);
//		Drawable rightImg = getResources().getDrawable(
//				R.drawable.map_upload_img);
		if (GlobalData.getRole().trim().equals("Role_Teacher")) {
			dl = TeacherActivity.dl;
		}else if (GlobalData.getRole().trim().equals("Role_Student")) {
			dl = StudentActivity.dl;
		}else{
			dl = HeadteacherActivity.dl;
		}
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				dl.open();
			}

		});
//		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {
//			@Override
//			public void onClick(View button) {
//				Intent intent = new Intent(getActivity(),
//						EmergeContactActivity.class);
//				intent.putExtra("role", "other");
//				startActivity(intent);
//			}
//		});

	}

	/**
	 * 初始化
	 * 
	 * @param view
	 */
	private void initView(View view) {
		emergencyHelper = new EmergencyHelper(getActivity());
		ListView listView = (ListView) view.findViewById(R.id.list_emerge);
		listView.setAdapter(new ArrayAdapter<String>(this.getActivity(),
				android.R.layout.simple_expandable_list_item_1,
				getStudentData()));
		listView.setOnItemClickListener(onStudentItemClickListener);

	}

	/**
	 * 各个角色的界面
	 * 
	 * @return 紧急情况的界面
	 */
	private List<String> getStudentData() {

		List<String> data = new ArrayList<String>();

		if (GlobalData.getRole().trim().equals("Role_Student")) {
			// 领导角色
			data.add("一键获取");
		}
		data.add("联系领导");
		data.add("联系导员");
		data.add("联系学生");
		data.add("一键广播");
		data.add("帮助说明");
		return data;
	}

	private AdapterView.OnItemClickListener onStudentItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i,
				long l) {
			Intent intent = new Intent();
			switch (i) {
			case 0:
				if (GlobalData.getRole().trim().equals("Role_Leader")) {
					// 领导联系领导
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "leader");
					startActivity(intent);
				} else if (GlobalData.getRole().trim()
						.equals("Role_HeadTeacher")) {
					// 辅导员联系领导
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "leader");
					startActivity(intent);
				} else {
					// 学生一键获取
					Leader.deletetable(emergencyHelper);
					HeadTeacher.deletetable(emergencyHelper);
					ClassMates.deletetable(emergencyHelper);
					getAllContacts();
				}

				break;
			case 1:

				if (GlobalData.getRole().trim().equals("Role_Leader")) {
					// 领导联系辅导员
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "teacher");
				} else if (GlobalData.getRole().trim()
						.equals("Role_HeadTeacher")) {
					// 辅导员联系辅导员
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "teacher");
				} else {
					// 学生联系领导
					intent = new Intent(getActivity(),
							EmergeContactActivity.class);
					intent.putExtra("role", "leader");
				}
				startActivity(intent);
				break;
			case 2:
				if (GlobalData.getRole().trim().equals("Role_Leader")) {
					// 领导联系学生
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "student");
				} else if (GlobalData.getRole().trim()
						.equals("Role_HeadTeacher")) {
					// 辅导员联系学生
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "student");
				} else {
					// 学生联系辅导员
					intent = new Intent(getActivity(),
							EmergeContactActivity.class);
					intent.putExtra("role", "teacher");
				}
				startActivity(intent);
				break;
			case 3:
				if (GlobalData.getRole().trim().equals("Role_Leader")) {
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "student");
				} else if (GlobalData.getRole().trim()
						.equals("Role_HeadTeacher")) {
					intent = new Intent(getActivity(),
							EmerConCheckActivity.class);
					intent.putExtra("role", "student");
				} else {
					// 学生联系学生
					intent = new Intent(getActivity(),
							EmergeContactActivity.class);
					intent.putExtra("role", "student");
				}
				startActivity(intent);
				break;
			case 4:

				break;
			}
		}
	};

	/**
	 * 领导：获取领导的联系方式
	 */
	public void getLeaderContact() {
		obj = new JSONObject();
		try {

			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					try {
						UrlServiceImpl urlService = new UrlServiceImpl();
						String leaderArray = urlService
								.getJsonContent(ConstantData.EMEGETLEADER);
						JSONObject jsonLeader = new JSONObject(leaderArray);// 防止非json数据传入
						for (int i = 0; i < jsonLeader.length(); i++) {
							String leaderarray = jsonLeader.getString("" + i);
							JSONObject jsonleader = new JSONObject(leaderarray);
							String name = jsonleader.getString("leaderName");
							String phone = jsonleader.getString("phone");
							Log.i(TAG, "leaderName==" + name);
							Log.i(TAG, "leaderPhone==" + phone);
							Leader.insertLeader(emergencyHelper, name, phone);
						}

					} catch (Exception e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(getActivity(), R.string.server_error);
			e.printStackTrace();
		}

	}

	/**
	 * 学生：获取所有人联系方式
	 */
	public void getAllContacts() {

		obj = new JSONObject();
		try {
			emerge_dialog = Tools.pd(getActivity(),
					"正在获取...");
			obj.put("userNickname", GlobalData.getUsername());

			Log.i("mytag", obj.toString());

			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonEmergeResult = urlService
							.sentParams2DataServer(obj,
									ConstantData.STUDENTGETEMERGE);
					Log.i("JSON",
							"jsonRegeditRst====" + jsonEmergeResult.toString());

					try {
						String classmateArray = jsonEmergeResult
								.getString("classmates");
						String teacherArray = jsonEmergeResult
								.getString("headTeacher");
						String leaderArray = jsonEmergeResult
								.getString("leader");

						JSONObject jsonClass = new JSONObject(classmateArray);// 防止非json数据传入

						for (int i = 0; i < jsonClass.length(); i++) {
							String classarray = jsonClass.getString("" + i);
							JSONObject jsonclass = new JSONObject(classarray);
							String name = jsonclass.getString("studentName");
							String phone = jsonclass.getString("phone");
							Log.i(TAG, "className==" + name);
							Log.i(TAG, "classPhone==" + phone);
							ClassMates.insertStudent(emergencyHelper, name,
									phone);
						}

						JSONObject jsonLeader = new JSONObject(leaderArray);// 防止非json数据传入
						for (int i = 0; i < jsonLeader.length(); i++) {
							String leaderarray = jsonLeader.getString("" + i);
							JSONObject jsonleader = new JSONObject(leaderarray);
							String name = jsonleader.getString("leaderName");
							String phone = jsonleader.getString("phone");
							Log.i(TAG, "leaderName==" + name);
							Log.i(TAG, "leaderPhone==" + phone);
							Leader.insertLeader(emergencyHelper, name, phone);
						}

						JSONObject jsonTeacher = new JSONObject(teacherArray);// 防止非json数据传入
						String name = jsonTeacher.getString("headTeacherName");
						String phone = jsonTeacher.getString("phone");
						Log.i(TAG, "teacherName==" + name);
						Log.i(TAG, "teacherPhone==" + phone);
						HeadTeacher.insertTeacher(emergencyHelper, name, phone);
						emerge_dialog.dismiss();
					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						emerge_dialog.dismiss();
						return;
					}
					if (jsonEmergeResult.equals("Success")) {
						handler.sendEmptyMessage(1);
						emerge_dialog.dismiss();
					} else if (jsonEmergeResult.equals("Fail")) {
						handler.sendEmptyMessage(2);
						emerge_dialog.dismiss();
					} else {
						emerge_dialog.dismiss();
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(getActivity(), R.string.server_error);
			e.printStackTrace();
		}

	}

}
