package com.beiyuan.appyujing.activity;

import java.util.ArrayList;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.adapter.ViewPagerAdapter;

public class WelcomeActivity extends MyActivity implements OnClickListener,
		OnPageChangeListener {

	private Button sureBtn;
	private Handler handler;

	private ViewPager viewPager;

	// 定义ViewPager适配器
	private ViewPagerAdapter vpAdapter;

	// 定义一个ArrayList来存放View
	private ArrayList<View> views;

	// 引导图片资源
	private static final int[] pics = { R.drawable.guide1, R.drawable.guide2,
			R.drawable.guide4, R.drawable.guide3 };

	private static final String TAG1 = "main1";

	private static final String TAG2 = "main2";

	// 底部小点的图片
	private ImageView[] points;

	private SharedPreferences sp;

	// 记录当前选中位置
	private int currentIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sp = this.getSharedPreferences("data", MODE_WORLD_READABLE);
		boolean isFirst = sp.getBoolean("first", true);

		handler = new Handler() {

			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1) {
					getWindow()
							.setFlags(
									WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,
									WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
					Intent intent = new Intent();
					intent.setClass(WelcomeActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();

					/**
					 * 设计忘记密码跳转
					 */
					// 主要是 用来拆分字符串

				} else if (msg.what == 2) {
					setContentView(R.layout.guide);
					sureBtn = (Button) findViewById(R.id.sure);

					// 定义ViewPager对象

					initView();

					initData();
					sureBtn.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
						
							getWindow().setFlags(
									WindowManager.LayoutParams.FLAG_FULLSCREEN,
									WindowManager.LayoutParams.FLAG_FULLSCREEN);
							Intent intent = new Intent();
							intent.setClass(WelcomeActivity.this, LoginActivity.class);
							startActivity(intent);
							finish();
							Log.i("mytag", "------------1");
						}

					});

				}
			}
		};

		if (isFirst) {

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.welcome);
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(3000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(2);
				}
			}.start();

			Editor editor = sp.edit();
			editor.putBoolean("first", false);
			editor.commit();

		} else {

			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
			setContentView(R.layout.welcome);
			new Thread() {

				public void run() {
					try {

						Thread.sleep(3000);

					} catch (Exception e) {

						e.printStackTrace();

					}
					handler.sendEmptyMessage(1);

				};
			}.start();
		}

	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		// 实例化ArrayList对象
		views = new ArrayList<View>();

		// 实例化ViewPager
		viewPager = (ViewPager) findViewById(R.id.viewPager);

		// 实例化ViewPager适配器
		vpAdapter = new ViewPagerAdapter(views);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// 定义一个布局并设置参数
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);

		// 初始化引导图片列表
		for (int i = 0; i < pics.length; i++) {
			ImageView iv = new ImageView(this);
			iv.setLayoutParams(mParams);
			iv.setImageResource(pics[i]);
			iv.setScaleType(ScaleType.FIT_XY);
			views.add(iv);
		}

		// 设置数据
		viewPager.setAdapter(vpAdapter);
		// 设置监听
		viewPager.setOnPageChangeListener(this);

		// 初始化底部小点
		initPoint();
	}

	/**
	 * 初始化底部小点
	 */
	private void initPoint() {
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.l1);

		points = new ImageView[pics.length];

		// 循环取得小点图片
		for (int i = 0; i < pics.length; i++) {
			// 得到一个LinearLayout下面的每一个子元素
			points[i] = (ImageView) linearLayout.getChildAt(i);
			// 默认都设为灰色
			// points[i].setEnabled(true);
			// 给每个小点设置监听
			points[i].setOnClickListener(this);
			// 设置位置tag，方便取出与当前位置对应
			points[i].setTag(i);
		}

		// 设置当面默认的位置
		currentIndex = 0;
		// 设置为白色，即选中状态
		// points[currentIndex].setEnabled(false);
		points[currentIndex].setImageResource(R.drawable.chengse);
		Log.i(TAG1, "初始化完成");
	}

	/**
	 * 当滑动状态改变时调用
	 */
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	/**
	 * 当当前页面被滑动时调用
	 */

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	/**
	 * 当新的页面被选中时调用
	 */

	@Override
	public void onPageSelected(int position) {
		// 设置底部小点选中状态
		setCurDot(position);
	}

	/**
	 * 通过点击事件来切换当前的页面
	 */
	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		setCurView(position);
		setCurDot(position);
	}

	/**
	 * 设置当前页面的位置
	 */
	private void setCurView(int position) {
		if (position < 0 || position >= pics.length) {
			return;
		}

		viewPager.setCurrentItem(position);
	}

	/**
	 * 设置当前的小点的位置
	 */
	private void setCurDot(int position) {
		if (position < 0 || position > pics.length - 1
				|| currentIndex == position) {
			return;
		}

		// points[position].setEnable(false);
		// points[currentIndex].setEnable(true);
		points[position].setImageResource(R.drawable.chengse);
		points[currentIndex].setImageResource(R.drawable.back5_1);
		Log.i(TAG2, "切换完成");
		currentIndex = position;

		if (position == pics.length - 1) {
			sureBtn.setVisibility(View.VISIBLE);
		} else {
			sureBtn.setVisibility(View.INVISIBLE);
		}
	}

}
