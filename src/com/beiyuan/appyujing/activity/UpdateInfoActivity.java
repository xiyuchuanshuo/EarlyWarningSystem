package com.beiyuan.appyujing.activity;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore.Images.ImageColumns;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.JsonParser;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.CircularImage;

public class UpdateInfoActivity extends MyActivity implements OnClickListener {

	private CircularImage headImg;
	private ImageView backImg;
	private TextView nicknameTxt;
	private ListView list_no_modify;
	private final static String TAG = "UpdateInfoActivity";
	private UrlService urlService = new UrlServiceImpl();
	private List<HashMap<String, String>> noModyDetail;
	private Handler handler;
	private ProgressDialog pdUpdate;
	final private int PICTURE_CHOOSE = 1;
	final private int TAKE_PHOTO = 2;
	private Bitmap img = null;
	AlertDialog photoDialog;
	private LinearLayout user_phone_lv;
	private LinearLayout user_password_lv;
	private TextView phoneTxt;
	private TextView passwordTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_info);
		initview();
		setListener();

		if (GlobalData.getRole().toString().equals("Role_Leader")) {
			getLeaderData();
		} else if (GlobalData.getRole().toString().equals("Role_Teacher")) {

			getTeacherData();
		} else if (GlobalData.getRole().toString().equals("Role_HeadTeacher")) {

			getHeadTeacherData();
		} else {

			getStudentData();
		}
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					// 学生查看不可修改信息
					SimpleAdapter adapterStuInfo = new SimpleAdapter(
							UpdateInfoActivity.this, noModyDetail,
							R.layout.update_info_no_item, new String[] {
									"user_id", "user_name" }, new int[] {
									R.id.user_id, R.id.user_name });

					list_no_modify.setAdapter(adapterStuInfo);
					setListViewHeightBasedOnChildren(list_no_modify);
					user_phone_lv.setVisibility(0);
					user_password_lv.setVisibility(0);
					pdUpdate.dismiss();
					break;

				case 2:
					user_phone_lv.setVisibility(0);
					user_password_lv.setVisibility(0);
					pdUpdate.dismiss();
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					break;
				}
			}
		};
	}

	public void initview() {
		headImg = (CircularImage) findViewById(R.id.get_head_img);
		nicknameTxt = (TextView) findViewById(R.id.get_user_nickname);
		backImg = (ImageView) findViewById(R.id.back);
		list_no_modify = (ListView) findViewById(R.id.no_modify_list);
		user_phone_lv = (LinearLayout) findViewById(R.id.phone_lv);
		user_password_lv = (LinearLayout) findViewById(R.id.password_lv);
		phoneTxt = (TextView) findViewById(R.id.user_phone);
		passwordTxt = (TextView) findViewById(R.id.user_password);
		passwordTxt.setText("********");
		nicknameTxt.setText(GlobalData.getUsername());
	}

	public void setListener() {
		headImg.setOnClickListener(this);
		backImg.setOnClickListener(this);
		phoneTxt.setOnClickListener(this);
		passwordTxt.setOnClickListener(this);
	}

	public void getLeaderData() {
		final JSONObject obj = new JSONObject();
		try {
			pdUpdate = Tools.pd(UpdateInfoActivity.this, "正在获取");
			obj.put("userNickname", GlobalData.getUsername());
			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadleader = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject upt_data = urlService.sentParams2DataServer(obj,
							ConstantData.SHOWLEADERINFO);

					try {
						String phoneNumber = upt_data.getString("phone");
						phoneTxt.setText(phoneNumber);
						handler.sendEmptyMessage(2);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						pdUpdate.dismiss();
					}

				}

			});
			threadleader.start();
		} catch (JSONException e) {
			Tools.mToast(UpdateInfoActivity.this, R.string.server_error);
			e.printStackTrace();
			pdUpdate.dismiss();
		}
	}

	public void getHeadTeacherData() {

		final JSONObject obj = new JSONObject();
		try {
			pdUpdate = Tools.pd(UpdateInfoActivity.this, "正在获取");
			obj.put("userNickname", GlobalData.getUsername());
			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadStudent = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject upt_data = urlService.sentParams2DataServer(obj,
							ConstantData.SHOWHEADTEAINFO);
					noModyDetail = JsonParser
							.parseUpdHeadTeaInfoResult(upt_data);
					try {
						String phoneNumber = upt_data.getString("phone");
						phoneTxt.setText(phoneNumber);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						pdUpdate.dismiss();
					}
					handler.sendEmptyMessage(1);

				}

			});
			threadStudent.start();
		} catch (JSONException e) {
			Tools.mToast(UpdateInfoActivity.this, R.string.server_error);
			e.printStackTrace();
			pdUpdate.dismiss();
		}

	}

	public void getTeacherData() {
		final JSONObject obj = new JSONObject();
		final HashMap<String, String> map1 = new HashMap<String, String>();
		final HashMap<String, String> map2 = new HashMap<String, String>();
		try {
			pdUpdate = Tools.pd(UpdateInfoActivity.this, "正在获取");
			obj.put("userNickname", GlobalData.getUsername());
			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadStudent = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject upt_data = urlService.sentParams2DataServer(obj,
							ConstantData.SHOWHEADTEAINFO);

					try {
						String name = upt_data.getString("name");
						String idCard = upt_data.getString("cardId");
						map1.put("user_name", "姓名:");
						map1.put("user_id", name);
						map2.put("user_name", "身份证号:");
						map2.put("user_id", idCard);
						noModyDetail.add(map1);
						noModyDetail.add(map2);

						String phoneNumber = upt_data.getString("phone");
						phoneTxt.setText(phoneNumber);

						handler.sendEmptyMessage(1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						pdUpdate.dismiss();
					}

				}

			});
			threadStudent.start();
		} catch (JSONException e) {
			Tools.mToast(UpdateInfoActivity.this, R.string.server_error);
			e.printStackTrace();
			pdUpdate.dismiss();
		}
	}

	public void getStudentData() {

		final JSONObject obj = new JSONObject();
		try {
			pdUpdate = Tools.pd(UpdateInfoActivity.this, "正在获取");
			obj.put("userNickname", GlobalData.getUsername());
			Log.i(TAG, "obj=====" + obj.toString());

			Thread threadStudent = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject upt_data = urlService.sentParams2DataServer(obj,
							ConstantData.SHOWSTUINFO);
					noModyDetail = JsonParser.parseUpdStuInfoResult(upt_data);
					try {
						String phoneNumber = upt_data.getString("phone");
						phoneTxt.setText(phoneNumber);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						pdUpdate.dismiss();
					}
					handler.sendEmptyMessage(1);

				}

			});
			threadStudent.start();
		} catch (JSONException e) {
			Tools.mToast(UpdateInfoActivity.this, R.string.server_error);
			e.printStackTrace();
			pdUpdate.dismiss();
		}

	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.get_head_img:
			showPhotoDialog();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.take_photo_btn:
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				Intent getImageByCamera = new Intent(
						"android.media.action.IMAGE_CAPTURE");
				startActivityForResult(getImageByCamera, TAKE_PHOTO);
				photoDialog.dismiss();
			} else {
				Toast.makeText(UpdateInfoActivity.this, "请确认已经插入SD卡",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.getphoto_btn:
			Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
			photoPickerIntent.setType("image/*");
			startActivityForResult(photoPickerIntent, PICTURE_CHOOSE);
			photoDialog.dismiss();
			break;
		case R.id.cancle_btn:
			photoDialog.dismiss();
			break;
		case R.id.user_phone:
			showUpdatePhone();
			break;
		case R.id.user_password:
			break;
		default:
			break;
		}
	}

	public void showPhotoDialog() {
		photoDialog = new AlertDialog.Builder(UpdateInfoActivity.this).create();
		LayoutInflater inflater = LayoutInflater.from(this);
		View contentView = inflater.inflate(R.layout.dialog_activity, null);
		photoDialog.setView(contentView);
		Button takePhotoBtn = (Button) contentView
				.findViewById(R.id.take_photo_btn);
		Button getPhotoBtn = (Button) contentView
				.findViewById(R.id.getphoto_btn);
		Button cancleBtn = (Button) contentView.findViewById(R.id.cancle_btn);
		takePhotoBtn.setOnClickListener(this);
		getPhotoBtn.setOnClickListener(this);
		cancleBtn.setOnClickListener(this);
		Window dialogWindow = photoDialog.getWindow();
		dialogWindow.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		photoDialog.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICTURE_CHOOSE) {

			if (data != null) {
				Cursor cursor = this.getContentResolver().query(data.getData(),
						null, null, null, null);
				cursor.moveToFirst();
				int idx = cursor.getColumnIndex(ImageColumns.DATA);
				String fileSrc = cursor.getString(idx);

				// just read size
				Options options = new Options();
				options.inJustDecodeBounds = true;
				img = BitmapFactory.decodeFile(fileSrc, options);

				// scale siz e to read
				options.inSampleSize = Math.max(1, (int) Math.ceil(Math.max(
						(double) options.outWidth / 1024f,
						(double) options.outHeight / 1024f)));
				options.inJustDecodeBounds = false;
				img = BitmapFactory.decodeFile(fileSrc, options);
				headImg.setImageBitmap(img);
			} else {
				Log.d("mytag", "idButSelPic Photopicker canceled");
			}
		} else if (requestCode == TAKE_PHOTO) {
			if (data != null) {
				Bundle bundle = data.getExtras();
				Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
				headImg.setImageBitmap(bitmap);
			} else {
				return;
			}
		}

	}

	// 动态设置listview的高度

	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		Log.i("mytag", "listAdapter==" + listAdapter.getCount());
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));

		Log.i("mytag", "totalHeight==" + totalHeight);
		Log.i("mytag",
				"listView.getDividerHeight()==" + listView.getDividerHeight());
		Log.i("mytag", "params.height==" + params.height);
		listView.setLayoutParams(params);
	}


	
	
	/**
	 * 更改电话号码
	 */
	public void showUpdatePhone() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.revicestudentphonenumber, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editPhone = (EditText) viewDialog.findViewById(R.id.phone_txt);
	
		builder.setCancelable(false);
		builder.setTitle("请输入号码");
		builder.setView(viewDialog);

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (editPhone.getText().toString().equals("")) {
					Tools.mToast(UpdateInfoActivity.this, "号码不能为空");
					return;
				}
				if (editPhone.getText().toString().length()>11) {
					Tools.mToast(UpdateInfoActivity.this, "号码不能超过11位");
					return;
				}
				phoneTxt.setText(editPhone.getText().toString());

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show();
	}
	
	/**
	 * 更改密码
	 */
	public void showUpdatePassword() {

		LayoutInflater inflater = LayoutInflater.from(this);
		View viewDialog = inflater.inflate(R.layout.revicestudentphonenumber, null);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText editPhone = (EditText) viewDialog.findViewById(R.id.phone_txt);
	
		builder.setCancelable(false);
		builder.setTitle("请输入号码");
		builder.setView(viewDialog);

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (editPhone.getText().toString().equals("")) {
					Tools.mToast(UpdateInfoActivity.this, "号码不能为空");
					return;
				}
				if (editPhone.getText().toString().length()>11) {
					Tools.mToast(UpdateInfoActivity.this, "号码不能超过11位");
					return;
				}
				phoneTxt.setText(editPhone.getText().toString());

			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			}
		});
		builder.show();
	}
}
