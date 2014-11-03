package com.beiyuan.appyujing.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.beiyuan.appyujing.TeacherActivity;
import com.beiyuan.appyujing.activity.EmergeContactActivity;
import com.beiyuan.appyujing.activity.NewsWebViewActivity;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.DateTools;
import com.beiyuan.appyujing.tools.Util;
import com.beiyuan.appyujing.view.DragLayout;
import com.beiyuan.appyujing.view.PullDownView;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.PullDownView.OnPullDownListener;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class NewsFragment extends Fragment implements OnPullDownListener,
		OnItemClickListener {
	private PullDownView mPullDownView;
	private ListView mListView;

	protected static final String TAG = "NewsYuJingPiceFragment";
	int page = 1;
	String stringPage;
	private SimpleAdapter mAdapter;
	View view;
	Handler handler;
	List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	JSONObject obj;
	private UrlService urlService;
	private boolean isFirst = true;
	private DragLayout dl;

	class getNews {
		int id;
		JSONObject Section;
		String Theme;
		String URL;
		String ReDate;
	}

	Gson gson = new Gson();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case -1:
					Toast.makeText(getActivity(), "内部数据错误", Toast.LENGTH_LONG)
							.show();
					break;
				case 1:
					mAdapter = new SimpleAdapter(getActivity(), list,
							R.layout.news_list_item, new String[] { "Theme" }, new int[] { R.id.news_item_title});
					Log.i(TAG, "list="+list.toString());
					mAdapter.notifyDataSetChanged();
					break;
				case 2:
					// Toast.makeText(NewsYuJingPiceFragment1.this.getActivity(),
					// "获取数据异常", Toast.LENGTH_LONG).show();

					break;
				}
			}
		};

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.news_yujing_pice_frag_1, null);

		mPullDownView = (PullDownView) view
				.findViewById(R.id.lv_news_yujing_bfxy_frag1);

		mPullDownView.setOnPullDownListener(this);

		mListView = mPullDownView.getListView();

		mListView.setOnItemClickListener(this);
		mAdapter = new SimpleAdapter(getActivity(), list,
				R.layout.news_list_item, new String[] { "Theme" }, new int[] { R.id.news_item_title});
		mListView.setAdapter(mAdapter);
		if (isFirst) {
			isFirst = false;
			if (Util.isNetworkAvailable(getActivity())) {

				Thread threadSet = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						Looper.prepare();

						obj = new JSONObject();

						try {

							stringPage = page + "";
							obj.put("index", stringPage);
							obj.put("sectionName", "学校风采");
							Log.i("NEWS", "obj=" + obj.toString());
							urlService = new UrlServiceImpl();
							String str = urlService.sentParams2News(obj);
							Log.i("NEWS", "str=" + str);

							if (str.equals("FAIL") || str == "FAIL") {
								Message message = new Message();
								message.what = 2;
								handler.sendMessage(message);
								return;
							}
							JSONObject jsonObject = new JSONObject(str);// 防止非json数据传入
																		// 程序异常
							Map<String, getNews> news = gson.fromJson(str,
									new TypeToken<Map<String, getNews>>() {
									}.getType());

							if (news != null) {
								Set<Map.Entry<String, getNews>> set = news
										.entrySet();
								for (Iterator<Map.Entry<String, getNews>> it = set
										.iterator(); it.hasNext();) {
									Map.Entry<String, getNews> entry = (Map.Entry<String, getNews>) it
											.next();
									Map<String, String> map = new HashMap<String, String>();
									map.put("Theme",
											news.get(entry.getKey()).Theme);
									map.put("Url", news.get(entry.getKey()).URL);
									String time = DateTools.dataFormat(news
											.get(entry.getKey()).ReDate);
									map.put("ReDate", time);
									list.add(map);
								}
							}

							Message message = new Message();
							message.what = 1;
							handler.sendMessage(message);

						} catch (Exception e) {
							Message message = new Message();
							message.what = -1;
							handler.sendMessage(message);
							Log.i(TAG, "e=" + e.toString());
						}
					}
				});
				threadSet.start();

			} else {
				Toast.makeText(getActivity(), "网络状态不可用！\n请先设置网络",
						Toast.LENGTH_LONG).show();
			}
			getMyView();
		}
		return view;
	}

	private void getMyView() {

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

	private TitleView mTitle;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mTitle = (TitleView) getView().findViewById(R.id.title);
		mTitle.setTitle("校园新闻");
		Drawable leftImg = getResources().getDrawable(R.drawable.side_btn_img);
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
		// mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {
		// @Override
		// public void onClick(View button) {
		// Intent intent = new Intent(getActivity(),
		// EmergeContactActivity.class);
		// intent.putExtra("role", "other");
		// startActivity(intent);
		// }
		// });

	}

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HashMap<String, Object> map = (HashMap<String, Object>) parent
				.getItemAtPosition(position);
		Log.i("NEWS", "position=" + position);
		Intent intent = new Intent();
		intent.setClass(getActivity(), NewsWebViewActivity.class);
		intent.putExtra("Url", map.get("Url").toString());
		startActivity(intent);
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 刷新的进度条RefreshComplete() **/
	@Override
	public void onRefresh() {

		if (Util.isNetworkAvailable(getActivity())) {

			Thread threadRefresh = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					obj = new JSONObject();
					try {
						// 隐藏并且禁用头部刷新
						mPullDownView.setHideHeader();
						page = 1;
						stringPage = page + "";
						obj.put("index", stringPage);
						obj.put("sectionName", "学校风采");
						urlService = new UrlServiceImpl();

						String str = urlService.sentParams2News(obj);
						Log.i(TAG, "onRefresh=" + str);
						if (str.equals("FAIL") || str == "FAIL") {
							Message message = new Message();
							message.what = 2;
							handler.sendMessage(message);
							mPullDownView.RefreshComplete();// 这个事线程安全的 可看源代码
							return;
						}
						JSONObject jsonObject = new JSONObject(str);
						Map<String, getNews> news = gson.fromJson(str,
								new TypeToken<Map<String, getNews>>() {
								}.getType());
						if (news != null) {
							List<Map<String, String>> templist = new ArrayList<Map<String, String>>();
							Set<Map.Entry<String, getNews>> set = news
									.entrySet();
							for (Iterator<Map.Entry<String, getNews>> it = set
									.iterator(); it.hasNext();) {
								Map.Entry<String, getNews> entry = (Map.Entry<String, getNews>) it
										.next();
								Map<String, String> map = new HashMap<String, String>();
								map.put("Theme", news.get(entry.getKey()).Theme);
								map.put("Url", news.get(entry.getKey()).URL);
								String time = DateTools.dataFormat(news
										.get(entry.getKey()).ReDate);
								map.put("ReDate", time);
								templist.add(map);
							}
							list = templist;
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
						Log.i(TAG, "e=" + e.toString());
					} finally {
						// 显示并且可以使用头部刷新
						mPullDownView.setShowHeader();
					}
				}
			});
			threadRefresh.start();

		} else {
			mPullDownView.RefreshComplete();
			Toast.makeText(getActivity(), "网络状态不可用！\n请先设置网络", Toast.LENGTH_LONG)
					.show();
		}
	}

	/** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
	@Override
	public void onMore() {
		if (Util.isNetworkAvailable(getActivity())) {

			Thread threadRefresh = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					obj = new JSONObject();
					try {
						Log.i(TAG, "page=" + page);
						page++;
						stringPage = page + "";
						Log.i(TAG, "stringPage=" + stringPage);
						obj.put("index", stringPage);
						obj.put("sectionName", "学校风采");
						urlService = new UrlServiceImpl();
						String str = urlService.sentParams2News(obj);
						Log.i(TAG, "onMore=" + str);
						if (str.equals("FAIL") || str == "FAIL") {
							Message message = new Message();
							message.what = 2;
							handler.sendMessage(message);
							mPullDownView.notifyDidMore();
							page--;
							return;
						}
						JSONObject jsonObject = new JSONObject(str);

						Map<String, getNews> news = gson.fromJson(str,
								new TypeToken<Map<String, getNews>>() {
								}.getType());
						if (news != null) {
							List<Map<String, String>> morelist = new ArrayList<Map<String, String>>();
							morelist.clear();
							Set<Map.Entry<String, getNews>> set = news
									.entrySet();
							for (Iterator<Map.Entry<String, getNews>> it = set
									.iterator(); it.hasNext();) {
								Map.Entry<String, getNews> entry = (Map.Entry<String, getNews>) it
										.next();
								Map<String, String> map = new HashMap<String, String>();
								map.put("Theme", news.get(entry.getKey()).Theme);
								map.put("Url", news.get(entry.getKey()).URL);
								String time = DateTools.dataFormat(news
										.get(entry.getKey()).ReDate);
								map.put("ReDate", time);
								morelist.add(map);
							}

							list.addAll(morelist);
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
						Log.i(TAG, "e=" + e.toString());
					}
				}
			});
			threadRefresh.start();

		} else {
			mPullDownView.notifyDidMore();
			Toast.makeText(getActivity(), "网络状态不可用！\n请先设置网络", Toast.LENGTH_LONG)
					.show();
		}
	}

}
