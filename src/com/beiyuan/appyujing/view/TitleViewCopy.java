package com.beiyuan.appyujing.view;

import com.beiyuan.appyujing.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class TitleViewCopy extends FrameLayout implements View.OnClickListener {

	private Button backBtn;
	private Button completeBtn;
	private Button dialogBtn;
	private TextView mTitle;
	private OnBackBtnClickListener mOnBackBtnClickListener;
	private OnCompleteBtnClickListener mOnCompleteBtnClickListener;
	private OnDialogBtnClickListener mOnDialogBtnClickListener;
	private OnUpdateBtnClickListener mOnUpdateBtnClickListener;

	public TitleViewCopy(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.title_view_copy, this, true);

		backBtn = (Button) findViewById(R.id.left_back_btn);
		backBtn.setVisibility(View.INVISIBLE);
		backBtn.setOnClickListener(this);
		completeBtn = (Button) findViewById(R.id.complete_btn);
		completeBtn.setVisibility(View.INVISIBLE);
		completeBtn.setOnClickListener(this);

		dialogBtn = (Button) findViewById(R.id.show_dialog_btn);
		dialogBtn.setVisibility(View.INVISIBLE);
		dialogBtn.setOnClickListener(this);

		mTitle = (TextView) findViewById(R.id.title_copy_text);
		mTitle.setVisibility(View.VISIBLE);
	}

	public void setTitle(String text) {
		mTitle.setVisibility(View.VISIBLE);
		mTitle.setText(text);
	}

	public void setTitle(int stringID) {
		mTitle.setVisibility(View.VISIBLE);
		mTitle.setText(stringID);
	}

	public interface OnBackBtnClickListener {
		public void onClick(View button);
	}

	public interface OnCompleteBtnClickListener {
		public void onClick(View button);
	}

	public interface OnDialogBtnClickListener {
		public void onClick(View button);
	}

	public interface OnUpdateBtnClickListener {
		public void onClick(View button);
	}

	public void setLeftButton(OnBackBtnClickListener listener) {
		backBtn.setVisibility(View.VISIBLE);
		mOnBackBtnClickListener = listener;
	}

	public void removeLeftButton() {
		backBtn.setVisibility(View.INVISIBLE);
		mOnBackBtnClickListener = null;
	}

	public void hiddenLeftButton() {
		backBtn.setVisibility(View.INVISIBLE);
	}

	public void showLeftButton() {
		backBtn.setVisibility(View.VISIBLE);
	}

	public void setRightButton(OnCompleteBtnClickListener listener) {
		completeBtn.setVisibility(View.VISIBLE);
		mOnCompleteBtnClickListener = listener;
	}

	public void setDialogButton(OnDialogBtnClickListener listener) {
		dialogBtn.setVisibility(View.VISIBLE);
		mOnDialogBtnClickListener = listener;
	}

	public void removeRightButton() {
		completeBtn.setVisibility(View.INVISIBLE);
		mOnCompleteBtnClickListener = null;
	}

	public void hiddenRightButton() {
		completeBtn.setVisibility(View.INVISIBLE);
	}

	public void showRightButton() {
		completeBtn.setVisibility(View.VISIBLE);
	}

	public void hiddenDialogButton() {
		dialogBtn.setVisibility(View.INVISIBLE);
	}

	public void showDialogButton() {
		dialogBtn.setVisibility(View.VISIBLE);
	}

	public TitleViewCopy(Context context) {
		this(context, null);
	}

	public TitleViewCopy(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.left_back_btn:
			if (mOnBackBtnClickListener != null)
				mOnBackBtnClickListener.onClick(v);
			break;
		case R.id.complete_btn:
			if (mOnCompleteBtnClickListener != null)
				mOnCompleteBtnClickListener.onClick(v);
			break;
		case R.id.show_dialog_btn:
			if (mOnDialogBtnClickListener != null)
				mOnDialogBtnClickListener.onClick(v);
			break;
		}
	}

}
