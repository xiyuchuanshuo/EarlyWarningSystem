package com.beiyuan.appyujing;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.beiyuan.appyujing.activity.AboutUsActivity;
import com.beiyuan.appyujing.activity.HelpActivity;
import com.beiyuan.appyujing.activity.UpdateInfoActivity;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.fragment.EmergencyFragment;
import com.beiyuan.appyujing.fragment.GaodeMapFragment;
import com.beiyuan.appyujing.fragment.NewsFragment;
import com.beiyuan.appyujing.fragment.VoiceFragment;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.tools.Util;
import com.beiyuan.appyujing.view.CircularImage;
import com.beiyuan.appyujing.view.DragLayout;
import com.beiyuan.appyujing.view.DragLayout.DragListener;
import com.nineoldandroids.view.ViewHelper;

public class StudentActivity extends FragmentActivity implements
		OnClickListener {

	// 加载各个模块
	public static final Class[] fragments = { VoiceFragment.class,
			NewsFragment.class, GaodeMapFragment.class, EmergencyFragment.class };
	public static FragmentTabHost mTabHost;
	public static RadioGroup mTabRg;

	private RadioButton mRadioButton1;
	private RadioButton mRadioButton2;
	private RadioButton mRadioButton3;
	private RadioButton mRadioButton4;

	public static DragLayout dl;
	private ListView listView;
	private CircularImage head_img;
	private TextView user_nickname;
	private TextView exit_txt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_student);

		dl = (DragLayout) findViewById(R.id.dl);
		dl.setDragListener(new DragListener() {
			@Override
			public void onOpen() {
				// lv.smoothScrollToPosition(new Random().nextInt(30));
			}

			@Override
			public void onClose() {
			}

			@Override
			public void onDrag(float percent) {
				animate(percent);
			}
		});

		initTheSideView();
		initView();
	}

	public void initTheSideView() {
		head_img = (CircularImage) findViewById(R.id.student_main_logo);
		user_nickname = (TextView) findViewById(R.id.student_main_name);
		exit_txt = (TextView) findViewById(R.id.exit_txt);
		user_nickname.setText(GlobalData.getUsername());

		listView = (ListView) findViewById(R.id.lv);
		listView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_expandable_list_item_1,
				getStudentData()));
		listView.setOnItemClickListener(onStudentItemClickListener);
		head_img.setOnClickListener(this);
		exit_txt.setOnClickListener(this);
	}

	/**
	 * 各个角色的界面
	 * 
	 * @return 紧急情况的界面
	 */
	private List<String> getStudentData() {

		List<String> data = new ArrayList<String>();
		data.add("个人信息");
		data.add("帮助界面");
		data.add("关于我们");
		return data;
	}

	private AdapterView.OnItemClickListener onStudentItemClickListener = new AdapterView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int i,
				long l) {
			Intent intent;
			switch (i) {
			case 0:
				intent = new Intent(StudentActivity.this,
						UpdateInfoActivity.class);
				startActivity(intent);
				break;
			case 1:
				intent = new Intent(StudentActivity.this, HelpActivity.class);
				startActivity(intent);

				break;
			case 2:
				intent = new Intent(StudentActivity.this, AboutUsActivity.class);
				startActivity(intent);

				break;
			}
		}
	};

	private void animate(float percent) {
		ViewGroup vg_left = dl.getVg_left();
		ViewGroup vg_main = dl.getVg_main();

		float f1 = 1 - percent * 0.3f;
		ViewHelper.setScaleX(vg_main, f1);
		ViewHelper.setScaleY(vg_main, f1);
		ViewHelper.setTranslationX(vg_left, -vg_left.getWidth() / 2.2f
				+ vg_left.getWidth() / 2.2f * percent);
		ViewHelper.setScaleX(vg_left, 0.5f + 0.5f * percent);
		ViewHelper.setScaleY(vg_left, 0.5f + 0.5f * percent);
		ViewHelper.setAlpha(vg_left, percent);

		int color = (Integer) Util.evaluate(percent,
				Color.parseColor("#ff000000"), Color.parseColor("#00000000"));
	}

	// 初始化fragment对象
	private void initView() {
		// TODO Auto-generated method stub
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup(StudentActivity.this, getSupportFragmentManager(),
				R.id.realtabcontent);
		// 得到fragment的个数
		int count = fragments.length;
		for (int i = 0; i < count; i++) {
			// 为每一个Tab按钮设置图标、文字和内容
			TabSpec tabSpec = mTabHost.newTabSpec(i + "").setIndicator(i + "");
			// 将Tab按钮添加进Tab选项卡中
			mTabHost.addTab(tabSpec, fragments[i], null);
		}

		mTabRg = (RadioGroup) findViewById(R.id.tab_rg_menu);
		mRadioButton1 = (RadioButton) findViewById(R.id.tab_rb_1);
		mRadioButton2 = (RadioButton) findViewById(R.id.tab_rb_2);
		mRadioButton3 = (RadioButton) findViewById(R.id.tab_rb_3);
		mRadioButton4 = (RadioButton) findViewById(R.id.tab_rb_4);
		mRadioButton1.setChecked(true);
		mTabRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.tab_rb_1:
					mTabHost.setCurrentTab(0);
					break;
				case R.id.tab_rb_2:
					mTabHost.setCurrentTab(1);

					break;
				case R.id.tab_rb_3:
					mTabHost.setCurrentTab(2);
					break;
				case R.id.tab_rb_4:
					mTabHost.setCurrentTab(3);
					break;
				default:
					break;
				}
			}
		});

		mTabHost.setCurrentTab(0);

	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Tools.exitBy2Click(this); // 调用双击退出函数
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.student_main_logo:

			break;
		case R.id.exit_txt:
			finish();
			break;

		default:
			break;
		}
	}
}
