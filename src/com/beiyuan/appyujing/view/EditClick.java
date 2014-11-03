package com.beiyuan.appyujing.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.beiyuan.appyujing.R;

public class EditClick extends LinearLayout implements View.OnClickListener {
	private EditText edt_input;
	private Button btn_click;
	private OnButtonClickListener mOnButtonClickListener;

	public EditClick(Context context) {
		super(context);
	}

	public interface OnButtonClickListener {
		public void onClick(View button);
	}

	public EditClick(Context context, AttributeSet attrs) {
		super(context, attrs);
		View view = LayoutInflater.from(context).inflate(
				R.layout.edittext_click, this, true);
		edt_input = (EditText) view.findViewById(R.id.edt_input);
		btn_click = (Button) this.findViewById(R.id.btn_click);
		btn_click.setOnClickListener(this);
	}

	/**
	 * @param str
	 *            设置输入框显示的默认值
	 */
	public void setHint(int str) {
		edt_input.setHint(str);
	}
	/**
	 * @param str
	 *            设置输入框显示的默认值
	 */
	public void setHint(String str) {
		edt_input.setHint(str);
	}

	@SuppressLint("NewApi")
	public void setBtnBackground(Drawable drawable) {
		btn_click.setBackground(drawable);
	}
	@SuppressLint("NewApi")
	public void setEditBackground(Drawable drawable) {
		edt_input.setBackground(drawable);
	}
	public void setEditDrawable(Drawable drawable) {
		edt_input.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
	}

	/**
	 * @return 获取输入框中的内容
	 */
	public String getText() {
		return edt_input.getText().toString();
	}

	public void setPassword(boolean flag) {
		if (flag) {
			//显示密码
			edt_input
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		} else {
			//隐藏密码
			edt_input.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	}

	public void setEditable(boolean flag) {
		edt_input.setEnabled(flag);
		edt_input.setFocusable(flag);
	}

	/**
	 * @return 设置输入框中的内容
	 */
	public void setText(String text) {
		edt_input.setText(text);
	}

	public void setRightButton(OnButtonClickListener listener) {
		btn_click.setVisibility(View.VISIBLE);
		mOnButtonClickListener = listener;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_click:
			if (mOnButtonClickListener != null)
				mOnButtonClickListener.onClick(v);
			break;

		default:
			break;
		}
	}

}
