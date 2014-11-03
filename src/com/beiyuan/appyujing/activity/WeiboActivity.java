package com.beiyuan.appyujing.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.adapter.StrudentInfoListAdapter;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.StudentInfo;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.Util;
import com.beiyuan.appyujing.view.PullDownView;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.PullDownView.OnPullDownListener;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;
import com.google.gson.Gson;

public class WeiboActivity extends MyActivity implements OnPullDownListener,
		OnItemClickListener {
	private PullDownView mPullDownView;
	private ListView mListView;
	String classID;
	String gradeID;
	String profession;
	String gradeName;
	int page = 0;
	String stringPage;
	Handler handler;
	private TitleView mTitle;
	JSONObject obj;
	private UrlService urlService;

	List<StudentInfo> list = new ArrayList<StudentInfo>();
	StrudentInfoListAdapter mAdapter;
	Gson gson = new Gson();
	private static final String TAG = "WeiboActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case -1:
					Toast.makeText(WeiboActivity.this, "网络状态不可用！\n请先设置网络",
							Toast.LENGTH_LONG).show();
					break;
				case 1:
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					Toast.makeText(WeiboActivity.this, "获取数据异常",
							Toast.LENGTH_LONG).show();
					break;
				}
			}
		};

		Bundle bundle = getIntent().getExtras();
		classID = bundle.getString("classID");
		gradeID = bundle.getString("gradeID");
		profession = bundle.getString("profession");
		gradeName = bundle.getString("gradeName");

		if (Util.isNetworkAvailable(WeiboActivity.this)) {
			Thread threadSet = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					obj = new JSONObject();

					try {

						stringPage = page + "";
						obj.put("classID", classID);
						obj.put("gradeID", gradeID);
						obj.put("profession", profession);
						obj.put("grade", gradeName);
						Log.i(TAG, "obj=" + obj.toString());
						Log.i(TAG, "http=" + ConstantData.MAPGETWEIBO);
						urlService = new UrlServiceImpl();
						JSONObject str = urlService.linkServer(obj,
								ConstantData.MAPGETWEIBO);
						Log.i(TAG, "--------------------------");
						Log.i(TAG, str.toString());
						Log.i(TAG, "--------------------------");
						JSONObject MapStudentInfo = str
								.getJSONObject("longLat");

						if (MapStudentInfo != null) {

							for (int i = 0; i < MapStudentInfo.length(); i++) {
								JSONObject j = MapStudentInfo
										.getJSONObject("longLat" + i);
								String longLatDate = j.getString("longLatDate");
								String geography = j.getString("geography");
								String speechURL = j.getString("speechURL");
								String studentNumber = j
										.getString("studentNumber");
								String studentName = j.getString("studentName");
								String word = j.getString("word");
								String longitude = j.getString("longitude");
								String latitude = j.getString("latitude");
								String thumbnailPictureURL = j
										.getString("thumbnailPictureURL");
								String pictureURL = j.getString("pictureURL");

								StudentInfo s = new StudentInfo(longLatDate,
										geography, speechURL, studentNumber,
										studentName, word, longitude, latitude,
										thumbnailPictureURL, pictureURL);
								list.add(s);

							}
							Log.i("WeiboActivity", "list=" + list.toString());
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
			Toast.makeText(WeiboActivity.this, "网络状态不可用！\n请先设置网络",
					Toast.LENGTH_LONG).show();
		}

		setContentView(R.layout.news_yujing_pice_frag_1);

		getMyView();
		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(gradeName + "级" + profession
				+ classID + "班");
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(R.drawable.refuce_img);
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				finish();
			}

		});
		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {
			@Override
			public void onClick(View button) {
				onRefresh();
			}
		});

	}


	private void getMyView() {

		mPullDownView = (PullDownView) findViewById(R.id.lv_news_yujing_bfxy_frag1);

		mPullDownView.setOnPullDownListener(this);

		mListView = mPullDownView.getListView();

		mListView.setOnItemClickListener(this);
		mAdapter = new StrudentInfoListAdapter(WeiboActivity.this, list,
				mPullDownView);
		mListView.setAdapter(mAdapter);

		// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
		mPullDownView.enableAutoFetchMore(true, 1);
		// 隐藏 并禁用尾部
		mPullDownView.setHideFooter();
		// 显示并启用自动获取更多
		mPullDownView.setShowFooter();
		// 隐藏并且禁用头部刷新
		mPullDownView.setHideHeader();
		// 显示并且可以使用头部刷新
		mPullDownView.setShowHeader();

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
	@Override
	public void onRefresh() {

		if (Util.isNetworkAvailable(WeiboActivity.this)) {

			Thread threadRefresh = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					obj = new JSONObject();
					try {
						// 隐藏并且禁用头部刷新
						mPullDownView.setHideHeader();
						page = 0;
						stringPage = page + "";
						obj.put("classID", classID);
						obj.put("gradeID", gradeID);
						obj.put("profession", profession);
						obj.put("grade", gradeName);
						Log.i(TAG, "obj=" + obj.toString());
						Log.i(TAG, "http=" + ConstantData.MAPGETWEIBO);
						urlService = new UrlServiceImpl();
						JSONObject str = urlService.linkServer(obj,
								ConstantData.MAPGETWEIBO);
						Log.i(TAG, "--------------------------");
						Log.i(TAG, str.toString());
						Log.i(TAG, "--------------------------");
						JSONObject MapStudentInfo = str
								.getJSONObject("longLat");
						List<StudentInfo> templist = new ArrayList<StudentInfo>();
						if (MapStudentInfo != null) {

							for (int i = 0; i < MapStudentInfo.length(); i++) {
								JSONObject j = MapStudentInfo
										.getJSONObject("longLat" + i);
								String longLatDate = j.getString("longLatDate");
								String geography = j.getString("geography");
								String speechURL = j.getString("speechURL");
								String studentNumber = j
										.getString("studentNumber");
								String studentName = j.getString("studentName");
								String word = j.getString("word");
								String longitude = j.getString("longitude");
								String latitude = j.getString("latitude");
								String thumbnailPictureURL = j
										.getString("thumbnailPictureURL");
								String pictureURL = j.getString("pictureURL");

								StudentInfo s = new StudentInfo(longLatDate,
										geography, speechURL, studentNumber,
										studentName, word, longitude, latitude,
										thumbnailPictureURL, pictureURL);
								templist.add(s);

							}
							list = templist;
							Log.i("WeiboActivity", "list=" + list.toString());
						}
						mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);

					} catch (Exception e) {
						mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码
						Message message = new Message();
						message.what = -1;
						handler.sendMessage(message);
					} finally {
						// 显示并且可以使用头部刷新
						mPullDownView.setShowHeader();
					}
				}
			});
			threadRefresh.start();

		} else {
			mPullDownView.RefreshComplete();
			Toast.makeText(WeiboActivity.this, "网络状态不可用！\n请先设置网络",
					Toast.LENGTH_LONG).show();
		}
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
	@Override
	public void onMore() {
		if (Util.isNetworkAvailable(WeiboActivity.this)) {

			Thread threadRefresh = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					obj = new JSONObject();
					try {

						page++;
						stringPage = page + "";
						obj.put("classID", classID);
						obj.put("gradeID", gradeID);
						obj.put("profession", profession);
						obj.put("grade", gradeName);
						Log.i(TAG, "obj=" + obj.toString());
						Log.i(TAG, "http=" + ConstantData.MAPGETWEIBO);
						urlService = new UrlServiceImpl();
						JSONObject str = urlService.linkServer(obj,
								ConstantData.MAPGETWEIBO);
						Log.i(TAG, "--------------------------");
						Log.i(TAG, str.toString());
						Log.i(TAG, "--------------------------");
						JSONObject MapStudentInfo = str
								.getJSONObject("longLat");

						if (MapStudentInfo != null) {

							for (int i = 0; i < MapStudentInfo.length(); i++) {
								JSONObject j = MapStudentInfo
										.getJSONObject("longLat" + i);
								String longLatDate = j.getString("longLatDate");
								String geography = j.getString("geography");
								String speechURL = j.getString("speechURL");
								String studentNumber = j
										.getString("studentNumber");
								String studentName = j.getString("studentName");
								String word = j.getString("word");
								String longitude = j.getString("longitude");
								String latitude = j.getString("latitude");
								String thumbnailPictureURL = j
										.getString("thumbnailPictureURL");
								String pictureURL = j.getString("pictureURL");
								StudentInfo s = new StudentInfo(longLatDate,
										geography, speechURL, studentNumber,
										studentName, word, longitude, latitude,
										thumbnailPictureURL, pictureURL);
								list.add(s);
							}
							Log.i("WeiboActivity", "list=" + list.toString());
						}

						mPullDownView.notifyDidMore();
						Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);

					} catch (Exception e) {
						mPullDownView.notifyDidMore();
						Message message = new Message();
						message.what = -1;
						handler.sendMessage(message);
					}
				}
			});
			threadRefresh.start();

		} else {
			mPullDownView.notifyDidMore();
			Toast.makeText(WeiboActivity.this, "网络状态不可用！\n请先设置网络",
					Toast.LENGTH_LONG).show();
		}
	}

}
