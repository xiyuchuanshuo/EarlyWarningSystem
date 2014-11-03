package com.beiyuan.appyujing.view;

import com.beiyuan.appyujing.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

public class TitleVoiceView extends FrameLayout implements View.OnClickListener {

	private Button backBtn;
	private Button voiceBtn;
	private EditText voiceText;
	private OnBackButtonClickListener mOnBackButtonClickListener;
	private OnVoicetButtonClickListener mOnVoiceButtonClickListener;

	public TitleVoiceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.title_voice_view, this, true);

		backBtn = (Button) findViewById(R.id.left_back);
		backBtn.setVisibility(View.INVISIBLE);
		backBtn.setOnClickListener(this);
		voiceBtn = (Button) findViewById(R.id.right_voice);
		voiceBtn.setVisibility(View.INVISIBLE);
		voiceBtn.setOnClickListener(this);

		voiceText = (EditText) findViewById(R.id.voice_text);
		voiceText.setVisibility(View.VISIBLE);
	}

	public interface OnBackButtonClickListener {
		public void onClick(View button);
	}

	public interface OnVoicetButtonClickListener {
		public void onClick(View button);
	}

	public interface OnContactButtonClickListener {
		public void onClick(View button);
	}

	public void setLeftButton(OnBackButtonClickListener listener) {
		backBtn.setVisibility(View.VISIBLE);
		mOnBackButtonClickListener = listener;
	}

	public void removeLeftButton() {
		backBtn.setVisibility(View.INVISIBLE);
		mOnBackButtonClickListener = null;
	}

	public void hiddenLeftButton() {
		backBtn.setVisibility(View.INVISIBLE);
	}

	public void showLeftButton() {
		backBtn.setVisibility(View.VISIBLE);
	}

	public void setRightButton(OnVoicetButtonClickListener listener) {
		voiceBtn.setVisibility(View.VISIBLE);
		mOnVoiceButtonClickListener = listener;
	}

	public void removeRightButton() {
		voiceBtn.setVisibility(View.INVISIBLE);
		mOnVoiceButtonClickListener = null;
	}

	public void hiddenRightButton() {
		voiceBtn.setVisibility(View.INVISIBLE);
	}

	public void showRightButton() {
		voiceBtn.setVisibility(View.VISIBLE);
	}

	public TitleVoiceView(Context context) {
		this(context, null);
	}

	public TitleVoiceView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_back:
			if (mOnBackButtonClickListener != null)
				mOnBackButtonClickListener.onClick(v);
			break;
		case R.id.right_voice:
			if (mOnVoiceButtonClickListener != null)
				mOnVoiceButtonClickListener.onClick(v);
			break;
		}
	}

}
