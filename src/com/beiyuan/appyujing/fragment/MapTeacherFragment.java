package com.beiyuan.appyujing.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.beiyuan.appyujing.HeadteacherActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.activity.WeiboActivity;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Util;
import com.beiyuan.appyujing.view.DragLayout;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;
import com.google.gson.Gson;

public class MapTeacherFragment extends Fragment implements OnItemClickListener {
	private ListView mListView;

	protected static final String TAG = "MapTeacherFragment";

	int page = 0;
	View view;
	Handler handler;
	JSONObject obj;
	private UrlService urlService;
	List<Map<String, String>> list;
	SimpleAdapter mAdapter;
	Gson gson = new Gson();

	String classID;
	String gradeID;
	String profession;
	String gradeName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.mapgetclass, null);

		getMyView();

		return view;
	}

	private void getMyView() {
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case -1:
					Toast.makeText(MapTeacherFragment.this.getActivity(),
							"网络状态不可用！\n请先设置网络", Toast.LENGTH_LONG).show();
					break;
				case 1:
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					Toast.makeText(MapTeacherFragment.this.getActivity(),
							"获取数据异常", Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
		list = new ArrayList<Map<String, String>>();
		if (Util.isNetworkAvailable(MapTeacherFragment.this.getActivity())) {
			Thread threadSet = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					obj = new JSONObject();
					try {
						obj.put("userNickname", GlobalData.getUsername());
						Log.i("test", obj.toString());
						urlService = new UrlServiceImpl();
						Log.i("test", "http=" + ConstantData.MAPGETCLASS);
						JSONObject jsonObject = urlService
								.sentParams2DataServer(obj,
										ConstantData.MAPGETCLASS);
						Log.i("test", "---------------------------");

						Log.i("test", jsonObject.toString());
						Log.i("test", "---------------------------");

						for (int i = 0; i < jsonObject.length(); i++) {
							HashMap<String, String> map = new HashMap<String, String>();
							String classarray = jsonObject.getString("" + i);
							JSONObject jsonStudentInfo = new JSONObject(
									classarray);
							String length = jsonStudentInfo
									.getString("longLatLength");
							String grade = jsonStudentInfo.getString("grade");
							JSONObject jsonGrade = new JSONObject(grade);
							gradeID = jsonGrade.getString("gradeID");
							gradeName = jsonGrade.getString("gradeName");
							profession = jsonGrade.getString("profession");
							classID = jsonGrade.getString("classID");
							String className = gradeName + "级" + profession
									+ classID + "班" + "       " + length;
							map.put("ClassName", className);
							map.put("gradeID", gradeID);
							map.put("classID", classID);
							map.put("profession", profession);
							map.put("gradeName", gradeName);
							list.add(map);
						}

						for (int i = 0; i < list.size(); i++) {
							Log.i("test", "list====" + list.get(i).toString());
						}
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);

					} catch (Exception e) {
						Message message = new Message();
						message.what = -1;
						handler.sendMessage(message);
						Log.i("MAPTEACHER", "e=" + e.toString());
					}
				}
			});
			threadSet.start();

		} else {
			Toast.makeText(MapTeacherFragment.this.getActivity(),
					"网络状态不可用！\n请先设置网络", Toast.LENGTH_LONG).show();
		}
		mListView = (ListView) view.findViewById(R.id.class_lv);

		mListView.setOnItemClickListener(this);
		mAdapter = new SimpleAdapter(MapTeacherFragment.this.getActivity(),
				list, R.layout.mapclassinfo, new String[] { "ClassName",
						"longLatLength" }, new int[] { R.id.classinfo_name,
						R.id.classinfo_num });
		mListView.setAdapter(mAdapter);

	}

	private TitleView mTitle;
	private DragLayout dl;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mTitle = (TitleView) getView().findViewById(R.id.title);
		mTitle.setTitle("班级紧急情况");
		Drawable leftImg = getResources().getDrawable(R.drawable.side_btn_img);
		Drawable rightImg = getResources().getDrawable(R.drawable.refuce_img);
		dl = HeadteacherActivity.dl;
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				dl.open();
			}

		});
		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {
			@Override
			public void onClick(View button) {
				
				getMyView();
			}
		});

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HashMap<String, Object> map = (HashMap<String, Object>) parent
				.getItemAtPosition(position);
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("classID", (String) map.get("classID"));
		bundle.putString("gradeID", (String) map.get("gradeID"));
		bundle.putString("profession", (String) map.get("profession"));
		bundle.putString("gradeName", (String) map.get("gradeName"));
		intent.putExtras(bundle);
		Log.i("test", "gradeID=" + gradeID);
		intent.setClass(MapTeacherFragment.this.getActivity(),
				WeiboActivity.class);
		startActivity(intent);
	}

}
