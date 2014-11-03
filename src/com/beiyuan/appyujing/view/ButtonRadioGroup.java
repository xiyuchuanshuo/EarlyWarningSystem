package com.beiyuan.appyujing.view;

import com.beiyuan.appyujing.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioGroup;

public class ButtonRadioGroup extends RadioGroup {

	public ButtonRadioGroup(Context context) {
		super(context);
	}

	public ButtonRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		changeButtonsImages();
	}

	private void changeButtonsImages() {
		int count = super.getChildCount();
		Log.i("mytag", "count===" + count);
		if (count > 1) {
			super.getChildAt(0).setBackgroundResource(R.drawable.attend_radio);
			for (int i = 1; i < count - 1; i++) {
				switch(i){
				case 1:
					super.getChildAt(i).setBackgroundResource(R.drawable.absence_radio);
					break;
				case 2:
					super.getChildAt(i).setBackgroundResource(R.drawable.late_radio);
					break;
				}
			}
			super.getChildAt(count - 1).setBackgroundResource(
					R.drawable.ask_radio);
		} else if (count == 1) {
			super.getChildAt(0).setBackgroundResource(R.drawable.attend_radio);
		}
	}
}