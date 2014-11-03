package com.beiyuan.appyujing.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import com.beiyuan.appyujing.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class DateTimePicker extends FrameLayout {
	private final NumberPicker mYearSpinner;
	private final NumberPicker mDateSpinner;
	private Calendar calendar;
	private int mYear;
	private String[] mDateDisplayValues = new String[7];
	private OnDateTimeChangedListener mOnDateTimeChangedListener;

	public DateTimePicker(Context context) {
		super(context);
		// 获取当前的年、月、日
		calendar = Calendar.getInstance();
		mYear = calendar.get(Calendar.YEAR);
		inflate(context, R.layout.data_dialog, this);

		mYearSpinner = (NumberPicker) this.findViewById(R.id.np_year);
		mYearSpinner.setMinValue(2013);
		mYearSpinner.setMaxValue(getCurrentYear());
		mYearSpinner.setValue(mYear);
		mYearSpinner.setOnValueChangedListener(mOnYearChangedListener);

		mDateSpinner = (NumberPicker) this.findViewById(R.id.np_date);
		mDateSpinner.setMinValue(0);
		mDateSpinner.setMaxValue(6);
		updateDateControl();
		mDateSpinner.setOnValueChangedListener(mOnDateChangedListener);
	}

	private NumberPicker.OnValueChangeListener mOnYearChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mYear = mYearSpinner.getValue();
			onDateTimeChanged();
		}
	};

	private NumberPicker.OnValueChangeListener mOnDateChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			calendar.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
			updateDateControl();
			onDateTimeChanged();
		}
	};

	private void updateDateControl() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(calendar.getTimeInMillis());
		cal.add(Calendar.DAY_OF_YEAR, -7 / 2 - 1);
		mDateSpinner.setDisplayedValues(null);
		for (int i = 0; i < 7; ++i) {
			cal.add(Calendar.DAY_OF_YEAR, 1);
			mDateDisplayValues[i] = (String) DateFormat.format("MM dd", cal);
		}
		mDateSpinner.setDisplayedValues(mDateDisplayValues);
		mDateSpinner.setValue(7 / 2);
		mDateSpinner.invalidate();
	}

	public interface OnDateTimeChangedListener {
		void onDateTimeChanged(DateTimePicker view, int year, int month,
				int day, int hour, int minute);
	}

	public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
		mOnDateTimeChangedListener = callback;
	}

	private void onDateTimeChanged() {
		if (mOnDateTimeChangedListener != null) {
			mOnDateTimeChangedListener.onDateTimeChanged(this, mYear,
					calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH), 0, 0);
		}
	}

	@SuppressLint("SimpleDateFormat")
	public int getCurrentYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String date = sdf.format(new java.util.Date());
		int year = Integer.parseInt(date);
		return year;
	}

}
