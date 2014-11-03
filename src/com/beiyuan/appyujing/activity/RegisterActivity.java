package com.beiyuan.appyujing.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;

public class RegisterActivity extends MyActivity {

	private TitleView mTitle;
	private RadioGroup user;
	private final String TAG = "RegisterActivity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rigster);
		initView();
	}

	private void initView() {

		user = (RadioGroup) findViewById(R.id.radiogroup);
		// 标题响应事件
		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.register_title);
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);
		Drawable rightImg = getResources().getDrawable(R.drawable.next_btn_img);
		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {

			@Override
			public void onClick(View button) {
				Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				finish();
				
			}

		});

		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {

			@SuppressLint("NewApi")
			@Override
			public void onClick(View button) {

				Intent intent = new Intent();
				Bundle bundle = new Bundle();

				if (user.getCheckedRadioButtonId() == R.id.radio_instructor) {
					// 辅导员
					intent.setClass(RegisterActivity.this,
							RegisterTeaFinishActivity.class);
					bundle.putString("flag", "headteacher");
					intent.putExtras(bundle);

				} else if (user.getCheckedRadioButtonId() == R.id.radio_teacher) {
					// 老师
					intent.setClass(RegisterActivity.this,
							RegisterTeaFinishActivity.class);
					bundle.putString("flag", "teacher");
					intent.putExtras(bundle);

				} else {
					// 学生
					intent.setClass(RegisterActivity.this,
							RegisterStuFinishActivity.class);
					intent.putExtras(bundle);
				}
				RegisterActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.push_left_in,
						R.anim.push_left_out);
			}
		});

	}
}
