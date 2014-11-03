package com.beiyuan.appyujing.view;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.tools.SoundMeter;

/**
 * 继承MapView重写onTouchEvent实现泡泡处理操作
 * 
 * @author
 * 
 */
class StudentInfoView extends TextView {

	Context context = null;

	static TextView mBtnRcd;

	static boolean isShosrt = false;
	static LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	static ImageView img1, sc_img1;
	static SoundMeter mSensor;
	static View rcChat_popup;
	static LinearLayout del_re;
	static ImageView volume;
	static boolean btn_vocie;
	static int flag = 1;
	static Handler mHandler = new Handler();
	static String voiceName;
	static long startVoiceT, endVoiceT;

	public StudentInfoView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public StudentInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public StudentInfoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 按下语音录制按钮时
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(context, "No SDCard", Toast.LENGTH_LONG).show();
			return false;
		}

		if (btn_vocie) {
			System.out.println("1");
			int[] location = new int[2];
			mBtnRcd.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
			int btn_rc_Y = location[1];
			int btn_rc_X = location[0];
			int[] del_location = new int[2];
			del_re.getLocationInWindow(del_location);
			int del_Y = del_location[1];
			int del_x = del_location[0];

			if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
				if (!Environment.getExternalStorageDirectory().exists()) {
					Toast.makeText(context, "No SDCard", Toast.LENGTH_LONG)
							.show();
					return false;
				}
				System.out.println("2");
				if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
					System.out.println("3");
					mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
					rcChat_popup.setVisibility(View.VISIBLE);
					voice_rcd_hint_loading.setVisibility(View.VISIBLE);
					voice_rcd_hint_rcding.setVisibility(View.GONE);
					voice_rcd_hint_tooshort.setVisibility(View.GONE);
					mHandler.postDelayed(new Runnable() {
						public void run() {
							if (!isShosrt) {
								voice_rcd_hint_loading.setVisibility(View.GONE);
								voice_rcd_hint_rcding
										.setVisibility(View.VISIBLE);
							}
						}
					}, 300);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					startVoiceT = System.currentTimeMillis();
					voiceName = "yujing.amr";
					start(voiceName);
					flag = 2;
				}
			} else if (event.getAction() == MotionEvent.ACTION_UP && flag == 2) {// 松开手势时执行录制完成
				System.out.println("4");
				mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					rcChat_popup.setVisibility(View.GONE);
					img1.setVisibility(View.VISIBLE);
					del_re.setVisibility(View.GONE);
					stop();
					flag = 1;
					File file = new File(
							android.os.Environment
									.getExternalStorageDirectory()
									+ "/"
									+ voiceName);
					if (file.exists()) {
						file.delete();
					}
				} else {

					voice_rcd_hint_rcding.setVisibility(View.GONE);
					stop();
					endVoiceT = System.currentTimeMillis();
					flag = 1;
					int time = (int) ((endVoiceT - startVoiceT) / 1000);
					Log.i("mylog", "startVoiceT=" + startVoiceT);
					Log.i("mylog", "endVoiceT=" + endVoiceT);
					Log.i("mylog", "(endVoiceT - startVoiceT)="
							+ (endVoiceT - startVoiceT));
					if (time < 1) {
						isShosrt = true;
						voice_rcd_hint_loading.setVisibility(View.GONE);
						voice_rcd_hint_rcding.setVisibility(View.GONE);
						voice_rcd_hint_tooshort.setVisibility(View.VISIBLE);
						mHandler.postDelayed(new Runnable() {
							public void run() {
								voice_rcd_hint_tooshort
										.setVisibility(View.GONE);
								rcChat_popup.setVisibility(View.GONE);
								isShosrt = false;
							}
						}, 500);
						return false;
					}
					// ChatMsgEntity entity = new ChatMsgEntity();
					// entity.setDate(getDate());
					// entity.setName("高富帅");
					// entity.setMsgType(false);
					// entity.setTime(time+"\"");
					// entity.setText(voiceName);
					// mDataArrays.add(entity);
					// mAdapter.notifyDataSetChanged();
					// mListView.setSelection(mListView.getCount() - 1);
					rcChat_popup.setVisibility(View.GONE);

				}
			}
			if (event.getY() < btn_rc_Y) {// 手势按下的位置不在语音录制按钮的范围内
				System.out.println("5");
				Animation mLitteAnimation = AnimationUtils.loadAnimation(
						context, R.anim.cancel_rc);
				Animation mBigAnimation = AnimationUtils.loadAnimation(context,
						R.anim.cancel_rc2);
				img1.setVisibility(View.GONE);
				del_re.setVisibility(View.VISIBLE);
				del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg);
				if (event.getY() >= del_Y
						&& event.getY() <= del_Y + del_re.getHeight()
						&& event.getX() >= del_x
						&& event.getX() <= del_x + del_re.getWidth()) {
					del_re.setBackgroundResource(R.drawable.voice_rcd_cancel_bg_focused);
					sc_img1.startAnimation(mLitteAnimation);
					sc_img1.startAnimation(mBigAnimation);
				}
			} else {

				img1.setVisibility(View.VISIBLE);
				del_re.setVisibility(View.GONE);
				del_re.setBackgroundResource(0);
			}
		}
		return super.onTouchEvent(event);
	}

	static final int POLL_INTERVAL = 300;

	Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	void updateDisplay(double signalEMA) {

		switch ((int) signalEMA) {
		case 0:
		case 1:
			volume.setImageResource(R.drawable.amp1);
			break;
		case 2:
		case 3:
			volume.setImageResource(R.drawable.amp2);

			break;
		case 4:
		case 5:
			volume.setImageResource(R.drawable.amp3);
			break;
		case 6:
		case 7:
			volume.setImageResource(R.drawable.amp4);
			break;
		case 8:
		case 9:
			volume.setImageResource(R.drawable.amp5);
			break;
		case 10:
		case 11:
			volume.setImageResource(R.drawable.amp6);
			break;
		default:
			volume.setImageResource(R.drawable.amp7);
			break;
		}
	}

}