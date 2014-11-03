package com.beiyuan.appyujing.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.adapter.QueryNameListAdapter;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.data.StudentData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.JsonParser;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleViewNew;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleViewNew.OnBackBtnClickListener;

/**
 * 快速查询模块，领导：获取该学院所有学生，辅导员：获取该班级所有学生
 * 
 * @author juan
 * 
 */
public class QuickQueryStudentActivity extends MyActivity implements
		OnScrollListener {
	private ListView list;// 声明ListView
	private List<StudentData> mDatas;// 数据
	private List<StudentData> loadDatas; // 加载数据
	private QueryNameListAdapter adapters; // 适配器
	private LinearLayout loadingLayout;// 底部加载布局
	private Thread mThread;// 加载线程
	private Context context;
	private boolean over;// 判断数据是否已全部加载
	private String api;
	private int unb = 1;
	private String name_college;
	private TitleView mTitleViewNew;
	private JSONObject rst_data;

	// 设置布局显示属性
	private LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.WRAP_CONTENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	// 设置布局显示目标最大化属性
	private LayoutParams FFlayoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,
			LinearLayout.LayoutParams.FILL_PARENT);
	private ProgressBar progressBar;
	private Bundle bundleb;
	private List<StudentData> listItem = new ArrayList<StudentData>();

	Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:// 判断是否数据已全部加载
				if (!over) {
					adapters.notifyDataSetChanged();
				} else {
					list.removeFooterView(loadingLayout);
					Tools.mToast(context, "数据加载完毕");
				}
				break;
			case 2:
//				adapters = new QueryNameListAdapter(context, mDatas);
//				list.setAdapter(adapters);
				adapters.updateListView(mDatas);
				list.removeFooterView(loadingLayout);
				Tools.mToast(context, "数据加载完毕");
				break;
			}
		};
	};
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.query_name);

		context = this.getApplicationContext();
		name_college = getIntent().getStringExtra("collegename");
		// 初始化底部加载视图
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		progressBar = new ProgressBar(context);
		progressBar.setPadding(0, 0, 15, 0);
		layout.addView(progressBar, mLayoutParams);
		TextView textView = new TextView(context);
		textView.setText("加载中...");
		textView.setGravity(Gravity.CENTER_VERTICAL);
		layout.addView(textView, FFlayoutParams);
		loadingLayout = new LinearLayout(context);
		loadingLayout.addView(layout, mLayoutParams);
		loadingLayout.setGravity(Gravity.CENTER);

		// 初始化Title
		mTitleViewNew = (TitleView) findViewById(R.id.title);
		mTitleViewNew.setTitle("快速查询");
		// 初始化ListView并设定事件
		list = (ListView) findViewById(R.id.query_name_list);
		list.addFooterView(loadingLayout);

		mDatas = new ArrayList<StudentData>();

		adapters = new QueryNameListAdapter(context, mDatas);

		list.setAdapter(adapters);
		list.setOnScrollListener(this);
		getListener();
		// 初始化相关变量
		over = false;
	}

	private void getListener() {
		// TODO Auto-generated method stub
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		mTitleViewNew.setLeftButton(leftImg, new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				finish();
			}

		});

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				// TODO Auto-generated method stub
				TextView txtNum = (TextView) view.findViewById(R.id.num_item);
				TextView txtName = (TextView) view.findViewById(R.id.name_item);
				String studentNumber = txtNum.getText().toString().trim();
				String studentName = txtName.getText().toString().trim();
				Intent intent = new Intent(QuickQueryStudentActivity.this,
						StudentDetailActivity.class);
				intent.putExtra("studentnumber", studentNumber);
				intent.putExtra("studentname", studentName);
				QuickQueryStudentActivity.this.startActivity(intent);
			}

		});
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

		if (firstVisibleItem + visibleItemCount == totalItemCount) {
			if (mThread == null || !mThread.isAlive()) {
				mThread = new Thread() {
					public void run() {

						UrlService service = new UrlServiceImpl();
						JSONObject obj = new JSONObject();
						try {
							if (GlobalData.getRole().equals("Role_Leader")) {
								obj.put("collegeName", name_college);
								obj.put("currentPage", unb + "");
								obj.put("pageSize", 10 + "");

								rst_data = service.sentParams2DataServer(obj,
										ConstantData.QUICKLEADER);

								loadDatas = JsonParser
										.parseQuickQueryStudentResult(rst_data);
								// 如果加载数据不为空，则将加载得到的数据添加到已有的mData数据中.否则数据加载完全
								if (loadDatas != null && loadDatas.size() > 0) {
									mDatas.addAll(loadDatas);
									System.out.println("mDatas.size()======"
											+ mDatas.size());
									loadDatas.clear();
								} else {
									unb = unb - 1;
									over = true;
								}
								unb++;
								myHandler.sendEmptyMessage(1);
							} else {
								obj.put("gradeID", name_college);

								rst_data = service.sentParams2DataServer(obj,
										ConstantData.QUICKTEACHER);
								mDatas = JsonParser
										.parseQuickQueryStudentResult(rst_data);
								myHandler.sendEmptyMessage(2);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					};
				};
				mThread.start();
			}
		}
	}
}