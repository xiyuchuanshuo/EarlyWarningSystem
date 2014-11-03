package com.beiyuan.appyujing.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.ChatMsgEntity;
import com.beiyuan.appyujing.tools.FileOperation;
import com.beiyuan.appyujing.tools.SoundMeter;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.TitleView;
import com.beiyuan.appyujing.view.TitleView.OnLeftButtonClickListener;
import com.beiyuan.appyujing.view.TitleView.OnRightButtonClickListener;
import com.google.gson.Gson;

public class MapSendMsgActivity extends MyActivity {

	private static final String TAG = "SAVE";
	private EditText msgEdit;
	private LinearLayout picturecut_Btn;
	private LinearLayout position_Btn;
	private ImageView voice_Btn;
	private TextView tv_position;
	private ImageView image_picture;
	private Button imageLoad;
	private TitleView mTitle;
	private View rcChat_popup;
	private int flag = 1;
	private LinearLayout del_re;
	private LinearLayout voice_rcd_hint_loading, voice_rcd_hint_rcding,
			voice_rcd_hint_tooshort;
	private Handler mHandler = new Handler();
	private boolean isShosrt = false;
	private ImageView img1, sc_img1;
	private long startVoiceT, endVoiceT;
	private String voiceName;
	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();
	private SoundMeter mSensor;
	private ImageView volume;
	private TextView chatContent;
	private ChatMsgEntity entity;
	private MediaPlayer mMediaPlayer = new MediaPlayer();

	private Handler handler;
	private UrlService urlService = new UrlServiceImpl();
	private String tpPicture = null;
	private String tpSpeech = null;
	private JSONObject mapSendObject;
	private JSONObject jsonLoginRst;
	private List<Map<String, Object>> mlistPicture = new ArrayList<Map<String, Object>>();
	private List<Map<String, Object>> mlistSpeech = new ArrayList<Map<String, Object>>();
	private List<String> fileList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_send_msg);
		initTopTitle();
		initview();
		setListener();
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {

				case 1:
					Tools.mToast(MapSendMsgActivity.this,
							R.string.map_send_success);
					break;
				case 2:
					Tools.mToast(MapSendMsgActivity.this,
							R.string.map_send_fail);
					break;
				case 3:
					Tools.mToast(MapSendMsgActivity.this,
							R.string.map_message_success);
					break;
				case 4:
					Tools.mToast(MapSendMsgActivity.this,
							R.string.map_message_fail);
					break;
				case -1:
					Tools.mToast(MapSendMsgActivity.this, R.string.server_error);
					break;
				}
			}
		};
	}

	public void initTopTitle() {
		mTitle = (TitleView) findViewById(R.id.title);
		mTitle.setTitle(R.string.map_send_title);
		Drawable rightImg = getResources().getDrawable(
				R.drawable.map_upload_img);
		Drawable leftImg = getResources().getDrawable(R.drawable.back_img);

		mTitle.setLeftButton(leftImg, new OnLeftButtonClickListener() {
			@Override
			public void onClick(View button) {
				File delPicture = new File(Environment
						.getExternalStorageDirectory(), "yujing.jpg");
				if (delPicture.exists()) {
					delPicture.delete();
				}
				File delSpeech = new File(Environment
						.getExternalStorageDirectory(), "yujing.amr");
				if (delSpeech.exists()) {
					delSpeech.delete();
				}

				finish();
			}

		});
		mTitle.setRightButton(rightImg, new OnRightButtonClickListener() {
			@Override
			public void onClick(View button) {
				submitMapMsg();
			}

			private void submitMapMsg() {

				try {

					tpPicture = FileOperation.File2String("yujing.jpg");
					tpSpeech = FileOperation.File2String("yujing.amr");
					// 地理位置信息发送
					Intent intent = getIntent();
					String address = intent.getStringExtra("address");
					String longtitude = intent.getStringExtra("longtitude");
					String latitude = intent.getStringExtra("latitude");

					// 消息和用户名称信息发送
					String word = msgEdit.getText().toString();
					String username = GlobalData.getUsername();

					// 存放图片和语音
					Map<String, Object> mapPicture = new HashMap<String, Object>();
					Map<String, Object> mapSpeech = new HashMap<String, Object>();
					JSONArray objk = new JSONArray();
					JSONArray objkPicture;
					JSONArray objkSpeech;
					mapSendObject = new JSONObject();

					if (tpSpeech.equals("") && tpPicture.equals("")
							&& address.equals("") && word.equals("")) {
						Toast.makeText(getBaseContext(), "输入的信息不能全部为空", 1)
								.show();
						return;
					} else if (tpPicture.equals("") && tpSpeech.equals("")) {
						fileList.clear();
						mlistPicture.clear();
						mlistSpeech.clear();

					} else if (tpPicture.equals("")) {
						fileList.clear();
						fileList.add("picture");
						mlistPicture.clear();
						mlistSpeech.clear();
						mapPicture.put("picture", tpPicture);
						mapPicture.put("fileExtension", "jpg");
						mlistPicture.add(mapPicture);

						// 将list数据通过Gson强制转换成String
						Gson gson = new Gson();
						String pictureString = gson.toJson(mlistPicture);

						// Object objkPicture1 = new Object();
						objkPicture = new JSONArray(pictureString);

						mapSendObject.put("picture", objkPicture);

					} else if (tpSpeech.equals("")) {
						fileList.clear();
						fileList.add("speech");
						mlistSpeech.clear();
						mapSpeech.put("speech", tpSpeech);
						mapSpeech.put("fileExtension", "amr");
						mlistSpeech.add(mapSpeech);
						// 将list数据通过Gson强制转换成String
						Gson gson = new Gson();
						String speechString = gson.toJson(mlistSpeech);

						// Object objkPicture1 = new Object();
						objkSpeech = new JSONArray(speechString);

						mapSendObject.put("speech", objkSpeech);

					} else {
						fileList.clear();
						fileList.add("picture");
						fileList.add("speech");

						mlistPicture.clear();
						mapPicture.put("picture", tpPicture);
						mapPicture.put("fileExtension", "jpg");
						mlistPicture.add(mapPicture);

						mlistSpeech.clear();
						mapSpeech.put("speech", tpSpeech);
						mapSpeech.put("fileExtension", "amr");
						mlistSpeech.add(mapSpeech);
						// 将list数据通过Gson强制转换成String
						Gson gson = new Gson();
						String pictureString = gson.toJson(mlistPicture);

						String speechString = gson.toJson(mlistSpeech);

						// Object objkPicture1 = new Object();
						objkPicture = new JSONArray(pictureString);
						objkSpeech = new JSONArray(speechString);

						mapSendObject.put("picture", objkPicture);
						mapSendObject.put("speech", objkSpeech);
					}

					for (int i = 0; i < fileList.size(); i++) {
						objk.put(fileList.get(i));
					}

					mapSendObject.put("longitude", longtitude);
					mapSendObject.put("geography", address);
					mapSendObject.put("userNickname", username);
					mapSendObject.put("latitude", latitude);
					mapSendObject.put("word", word);
					mapSendObject.put("fileKind", objk);
					Log.i("MAP", "objk=" + objk.toString());

					Log.i("MAP", "URL=" + ConstantData.SENDMAPSTUDENTINFO);
					Log.i("MAP", "mapSendObject=" + mapSendObject.toString());

					Log.i("mytag", "objk=" + objk);

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				new Thread(new Runnable() {

					public void run() {
						Looper.prepare();

						jsonLoginRst = urlService.sentParams2DataServer(
								mapSendObject, ConstantData.SENDMAPSTUDENTINFO);
						Log.i("MAP",
								"jsonLoginRst_image=" + jsonLoginRst.toString());
						String strLoginRst = null;
						try {

							strLoginRst = jsonLoginRst.getString("Status");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if (TextUtils.isEmpty(strLoginRst)) {
							handler.sendEmptyMessage(-1);
							return;
						}
						if (strLoginRst.equals("Success")) {
							handler.sendEmptyMessage(3);
						} else if (strLoginRst.equals("Fail")) {
							handler.sendEmptyMessage(4);
						}
					}
				}).start();

			}

			private static final int POLL_INTERVAL = 300;

			private Runnable mSleepTask = new Runnable() {
				public void run() {
					stop();
				}
			};
			private Runnable mPollTask = new Runnable() {
				public void run() {
					double amp = mSensor.getAmplitude();
					updateDisplay(amp);
					mHandler.postDelayed(mPollTask, POLL_INTERVAL);

				}
			};

			private void start(String name) {
				mSensor.start(name);
				mHandler.postDelayed(mPollTask, POLL_INTERVAL);
			}

			private void stop() {
				mHandler.removeCallbacks(mSleepTask);
				mHandler.removeCallbacks(mPollTask);
				mSensor.stop();
				volume.setImageResource(R.drawable.amp1);
			}

			private void updateDisplay(double signalEMA) {

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

		});

	}

	public void initview() {
		tv_position = (TextView) findViewById(R.id.tv_position);
		chatContent = (TextView) findViewById(R.id.chatcontent);
		imageLoad = (Button) findViewById(R.id.imageload);
		msgEdit = (EditText) findViewById(R.id.msgEdit);
		picturecut_Btn = (LinearLayout) findViewById(R.id.btn_pCut);
		position_Btn = (LinearLayout) findViewById(R.id.btn_position);
		voice_Btn = (ImageView) findViewById(R.id.btn_voice);
		image_picture = (ImageView) findViewById(R.id.image_picture);

		rcChat_popup = this.findViewById(R.id.chat_ui);
		del_re = (LinearLayout) this.findViewById(R.id.del_re);
		voice_rcd_hint_rcding = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_rcding);
		voice_rcd_hint_loading = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_loading);
		voice_rcd_hint_tooshort = (LinearLayout) this
				.findViewById(R.id.voice_rcd_hint_tooshort);
		img1 = (ImageView) this.findViewById(R.id.img1);
		sc_img1 = (ImageView) this.findViewById(R.id.sc_img1);
		mSensor = new SoundMeter();
		volume = (ImageView) this.findViewById(R.id.volume);
	}

	public void setListener() {

		// 语音播放
		chatContent.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (entity.getText().contains(".amr")) {
					playMusic(android.os.Environment
							.getExternalStorageDirectory()
							+ "/"
							+ entity.getText());
				}
			}
		});

		// 图片选择
		picturecut_Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ShowPickDialog();
			}
		});

		// 图片长按删除
		picturecut_Btn.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// AlertDialog dialog = new AlertDialog
				Log.i("LongClick", "删除图片");
				File delPicture = new File(Environment
						.getExternalStorageDirectory(), "yujing.jpg");
				if (delPicture.exists()) {
					delPicture.delete();
				}

				image_picture.setBackgroundResource(R.drawable.main_img);
				return false;
			}
		});

		imageLoad.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ShowPickDialog();
			}
		});

		imageLoad.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Log.i("LongClick", "删除图片");
				File delPicture = new File(Environment
						.getExternalStorageDirectory(), "yujing.jpg");
				if (delPicture.exists()) {
					delPicture.delete();
				}

				image_picture.setBackgroundResource(R.drawable.main_img);
				return false;
			}
		});

		// 得到位置信息按钮
		position_Btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = getIntent();
				String address = intent.getStringExtra("address");
				String longtitude = intent.getStringExtra("longtitude");
				String latitude = intent.getStringExtra("latitude");
				if (address.equals("")) {
					Toast.makeText(getBaseContext(), "亲，你还没有定位哦", 1).show();
					// tv_position.setText(address + "\n" + "(经度：" + longtitude
					// + ",纬度："
					// + latitude + ")");
				} else {
					String lon = longtitude.substring(0,
							longtitude.length() - 4);
					String lat = latitude.substring(0, latitude.length() - 3);

					Log.i("MAPSTUDENT", "address = " + address + "longtitude="
							+ longtitude + "latitude=" + latitude);
					tv_position.setText(address + "\n" + "(经度：" + lon + ",纬度："
							+ lat + ")");
				}
			}
		});

		voice_Btn.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				// 按下语音录制按钮时返回false执行父类OnTouch
				Log.i("myVoide", "点击");
				return false;
			}
		});
	}

	/**
	 * 选择提示对话框
	 */
	private void ShowPickDialog() {
		new AlertDialog.Builder(this)
				.setTitle("设置头像...")
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_PICK, null);

						intent.setDataAndType(
								MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
								"image/*");
						startActivityForResult(intent, 1);

					}
				})
				.setPositiveButton("拍照", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
						/**
						 * 下面这句还是老样子，调用快速拍照功能，至于为什么叫快速拍照，大家可以参考如下官方
						 * 文档，you_sdk_path/docs/guide/topics/media/camera.html
						 * 我刚看的时候因为太长就认真看，其实是错的，这个里面有用的太多了，所以大家不要认为
						 * 官方文档太长了就不看了，其实是错的，这个地方小马也错了，必须改正
						 */
						Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						// 下面这句指定调用相机拍照后的照片存储的路径
						intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
								.fromFile(new File(Environment
										.getExternalStorageDirectory(),
										"yujing.jpg")));
						startActivityForResult(intent, 2);
					}
				}).show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		// 如果是直接从相册获取
		case 1:
			if (resultCode == -1) {
				startPhotoZoom(data.getData());
			}
			break;
		// 如果是调用相机拍照时
		case 2:
			if (resultCode == -1) {
				File temp = new File(Environment.getExternalStorageDirectory()
						+ "/yujing.jpg");
				startPhotoZoom(Uri.fromFile(temp));
			}
			break;
		// 取得裁剪后的图片
		case 3:
			/**
			 * 非空判断大家一定要验证，如果不验证的话， 在剪裁之后如果发现不满意，要重新裁剪，丢弃
			 * 当前功能时，会报NullException，小马只 在这个地方加下，大家可以根据不同情况在合适的 地方做判断处理类似情况
			 * 
			 */
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		/*
		 * 至于下面这个Intent的ACTION是怎么知道的，大家可以看下自己路径下的如下网页
		 * yourself_sdk_path/docs/reference/android/content/Intent.html
		 * 直接在里面Ctrl+F搜：CROP ，之前小马没仔细看过，其实安卓系统早已经有自带图片裁剪功能, 是直接调本地库的，小马不懂C C++
		 * 这个不做详细了解去了，有轮子就用轮子，不再研究轮子是怎么 制做的了...吼吼
		 */
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);
//		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 400);
		intent.putExtra("outputY", 400);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, 3);
	}

	/**
	 * 保存裁剪之后的图片数据
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			image_picture.setBackgroundDrawable(drawable);
			saveBitmap(photo);

		}
	}

	private void saveBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		Log.e(TAG, "保存图片");
		File f = new File(Environment.getExternalStorageDirectory(),
				"/yujing.jpg");
		if (f.exists()) {
			f.delete();
		}
		try {
			FileOutputStream out = new FileOutputStream(f);
			bm.compress(Bitmap.CompressFormat.JPEG, 40, out);
			out.flush();
			out.close();
			Log.i(TAG, "已经保存");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 按下语音录制按钮时
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!Environment.getExternalStorageDirectory().exists()) {
			Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
			return false;
		}

		System.out.println("1");
		int[] location = new int[2];
		voice_Btn.getLocationInWindow(location); // 获取在当前窗口内的绝对坐标
		int btn_rc_Y = location[1];
		int btn_rc_X = location[0];
		int[] del_location = new int[2];
		del_re.getLocationInWindow(del_location);
		int del_Y = del_location[1];
		int del_x = del_location[0];
		int pointX = (int) event.getX();
		int pointY = (int) event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN && flag == 1) {
			if (!Environment.getExternalStorageDirectory().exists()) {
				Toast.makeText(this, "No SDCard", Toast.LENGTH_LONG).show();
				return false;
			}
			System.out.println("2");
			if (event.getY() > btn_rc_Y && event.getX() > btn_rc_X) {// 判断手势按下的位置是否是语音录制按钮的范围内
				System.out.println("3");
				// mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_pressed);
				rcChat_popup.setVisibility(View.VISIBLE);
				voice_rcd_hint_loading.setVisibility(View.VISIBLE);
				voice_rcd_hint_rcding.setVisibility(View.GONE);
				voice_rcd_hint_tooshort.setVisibility(View.GONE);

				mHandler.postDelayed(new Runnable() {
					public void run() {
						if (!isShosrt) {
							voice_rcd_hint_loading.setVisibility(View.GONE);
							voice_rcd_hint_rcding.setVisibility(View.VISIBLE);
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
			// mBtnRcd.setBackgroundResource(R.drawable.voice_rcd_btn_nor);
			if (event.getY() >= del_Y
					&& event.getY() <= del_Y + del_re.getHeight()
					&& event.getX() >= del_x
					&& event.getX() <= del_x + del_re.getWidth()) {
				rcChat_popup.setVisibility(View.GONE);
				chatContent.setVisibility(View.GONE);
				img1.setVisibility(View.VISIBLE);
				del_re.setVisibility(View.GONE);
				stop();
				flag = 1;
				File file = new File(
						android.os.Environment.getExternalStorageDirectory()
								+ "/" + voiceName);
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
							voice_rcd_hint_tooshort.setVisibility(View.GONE);
							rcChat_popup.setVisibility(View.GONE);
							isShosrt = false;
						}
					}, 1000);
					return false;
				}
				entity = new ChatMsgEntity();
				entity.setTime(time + "\"");
				entity.setText(voiceName);
				rcChat_popup.setVisibility(View.GONE);
				if (entity.getText().contains(".amr")) {
					chatContent.setVisibility(View.VISIBLE);
					chatContent.setText("");
					chatContent.setCompoundDrawablesWithIntrinsicBounds(0, 0,
							R.drawable.chatto_voice_playing, 0);
					chatContent.setText(entity.getTime());
				}

			}
		}

		if (event.getY() < btn_rc_Y) {

			// 手势按下的位置不在语音录制按钮的范围内
			System.out.println("5");
			Animation mLitteAnimation = AnimationUtils.loadAnimation(this,
					R.anim.cancel_rc);
			Animation mBigAnimation = AnimationUtils.loadAnimation(this,
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

		return super.onTouchEvent(event);
	}

	private static final int POLL_INTERVAL = 300;

	private Runnable mSleepTask = new Runnable() {
		public void run() {
			stop();
		}
	};
	private Runnable mPollTask = new Runnable() {
		public void run() {
			double amp = mSensor.getAmplitude();
			updateDisplay(amp);
			mHandler.postDelayed(mPollTask, POLL_INTERVAL);

		}
	};

	private void start(String name) {
		mSensor.start(name);
		mHandler.postDelayed(mPollTask, POLL_INTERVAL);
	}

	private void stop() {
		mHandler.removeCallbacks(mSleepTask);
		mHandler.removeCallbacks(mPollTask);
		mSensor.stop();
		volume.setImageResource(R.drawable.amp1);
	}

	private void updateDisplay(double signalEMA) {

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

	/**
	 * @Description
	 * @param name
	 */
	private void playMusic(String name) {
		try {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
