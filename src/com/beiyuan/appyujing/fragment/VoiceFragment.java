package com.beiyuan.appyujing.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.beiyuan.appyujing.HeadteacherActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.TeacherActivity;
import com.beiyuan.appyujing.activity.EmerConCheckActivity;
import com.beiyuan.appyujing.activity.EmergeContactActivity;
import com.beiyuan.appyujing.activity.LoginActivity;
import com.beiyuan.appyujing.activity.NewsWebViewActivity;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.database.EmergencyHelper;
import com.beiyuan.appyujing.database.table.ClassMates;
import com.beiyuan.appyujing.database.table.HeadTeacher;
import com.beiyuan.appyujing.database.table.Leader;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.JsonParser;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.DragLayout;
import com.beiyuan.appyujing.view.TitleVoiceView;
import com.beiyuan.appyujing.view.TitleVoiceView.OnBackButtonClickListener;
import com.beiyuan.appyujing.view.TitleVoiceView.OnVoicetButtonClickListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechListener;
import com.iflytek.cloud.speech.SpeechUser;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/**
 * 语音功能模块：打电话、发短信、看新闻
 * 
 * @author juan
 * 
 */
public class VoiceFragment extends Fragment {

	private TitleVoiceView mTitleVoiceView;
	private SharedPreferences mSharedPreferences;
	private final String APP_ID = "appid=53230169";
	private RecognizerDialog dialog;
	private EditText textContent;
	private Intent intentCall;
	private SimpleAdapter adapterCall;
	private List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private ListView listview;
	private JSONObject obj;
	private UrlService urlService = new UrlServiceImpl();

	private Intent intentMessage;
	private SimpleAdapter adapterMessage;
	private EmergencyHelper emergencyHelper = new EmergencyHelper(getActivity());
	private Handler handler;
	private final static String TAG = "VoiceFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View parentView = inflater.inflate(R.layout.voice_call_view, container,
				false);
		initView(parentView);
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 5:
					Tools.mToast(getActivity(), R.string.server_error);
					break;
				}
			}
		};

		return parentView;

	}
	private DragLayout dl;
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
		textContent = (EditText) getView().findViewById(R.id.voice_text);
		mTitleVoiceView = (TitleVoiceView) getView().findViewById(
				R.id.title_voice_call);
		String s = GlobalData.getRole();
		if (GlobalData.getRole().trim().equals("Role_Teacher")) {
			dl = TeacherActivity.dl;
		}else if (GlobalData.getRole().trim().equals("Role_Student")) {
			dl = StudentActivity.dl;
		}else{
			dl = HeadteacherActivity.dl;
		}
		
		mTitleVoiceView.setLeftButton(new OnBackButtonClickListener() {

			@Override
			public void onClick(View button) {
				dl.open();
			}
		});
		mTitleVoiceView.setRightButton(new OnVoicetButtonClickListener() {

			@Override
			public void onClick(View button) {
				// TODO Auto-generated method stub
				textContent.setText("");
				showContactsView();
			}
		});
	}

	public void initView(View view) {
		listview = (ListView) view.findViewById(R.id.call_listview);
	}

	public void showContactsView() {
		// 获取引擎参数
		String engine = mSharedPreferences.getString(
				getString(R.string.preference_key_iat_engine),
				getString(R.string.preference_default_iat_engine));
		// 用户登录
		SpeechUser.getUser().login(getActivity(), null, null, APP_ID,
				loginListener);
		// 默认dialog
		dialog = new RecognizerDialog(getActivity());
		dialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
		dialog.setParameter(SpeechConstant.DOMAIN, engine);
		// 去除所有标点符号
		// dialog.setParameter(SpeechConstant.PARAMS, "asr_ptt=0");
		// 设置采样率参数
		dialog.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
		dialog.setListener(recognizerDialogListener);
		dialog.show();

	}

	private SpeechListener loginListener = new SpeechListener() {

		@Override
		public void onCompleted(SpeechError arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onData(byte[] arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onEvent(int arg0, Bundle arg1) {
			// TODO Auto-generated method stub

		}

	};
	RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener() {

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			// TODO Auto-generated method stub

			String text = JsonParser.parseIatResult(results.getResultString());
			// 去标点符号
			textContent.append(text.replaceAll("。", ""));
			Log.e("mytag", "textContent=====" + text);
			// 定位编辑的光标位置
			textContent.setSelection(textContent.length());
			// 判别
			judgeByName();

		}

		@Override
		public void onError(SpeechError arg0) {
			// TODO Auto-generated method stub

		}
	};

	public void judgeByName() {

		String name = textContent.getText().toString().trim();
		// 条件
		String reg_call = "给.*?打电话";
		String reg_callname = "打电话";
		String reg_callchar = "给";

		String reg_contact_leader = "联系领导";
		String reg_contact_teacher = "联系导员";
		String reg_contact_student = "联系同学";

		String reg_message = "给.*?发短信";
		String reg_messagename = "发短信";

		Pattern call = Pattern.compile(reg_call);
		Pattern message = Pattern.compile(reg_message);
		Pattern contact_leader = Pattern.compile(reg_contact_leader);
		Pattern contact_teacher = Pattern.compile(reg_contact_teacher);
		Pattern contact_student = Pattern.compile(reg_contact_student);
		Intent intent = new Intent();

		if (call.matcher(name).find()) {
			String startName = name.replaceAll(reg_callname, "");
			String finishName = startName.replaceAll(reg_callchar, "");
			searchCallContact(finishName);

		} else if (message.matcher(name).find()) {
			String startName = name.replaceAll(reg_messagename, "");
			String finishName = startName.replaceAll(reg_callchar, "");
			searchMessageContact(finishName);
		} else if (contact_leader.matcher(name).find()) {
			if (GlobalData.getRole().trim().equals("Role_Leader")) {
				// 领导联系领导
				intent.setClass(getActivity(), EmerConCheckActivity.class);

			} else if (GlobalData.getRole().trim().equals("Role_Headteacher")) {
				// 辅导员联系领导
				intent.setClass(getActivity(), EmerConCheckActivity.class);
			} else {
				intent.setClass(getActivity(), EmergeContactActivity.class);
			}
			intent.putExtra("role", "leader");
			startActivity(intent);

		} else if (contact_teacher.matcher(name).find()) {

			if (GlobalData.getRole().trim().equals("Role_Leader")) {
				// 领导联系辅导员
				intent.setClass(getActivity(), EmerConCheckActivity.class);
			} else if (GlobalData.getRole().trim().equals("Role_Headteacher")) {
				// 辅导员联系辅导员
				intent.setClass(getActivity(), EmerConCheckActivity.class);
			} else {
				intent.setClass(getActivity(), EmergeContactActivity.class);
			}
			intent.putExtra("role", "teacher");
			startActivity(intent);

		} else if (contact_student.matcher(name).find()) {
			if (GlobalData.getRole().trim().equals("Role_Leader")) {
				// 领导联系学生
				intent.setClass(getActivity(), EmerConCheckActivity.class);
			} else if (GlobalData.getRole().trim().equals("Role_Headteacher")) {
				// 辅导员联系学生
				intent.setClass(getActivity(), EmerConCheckActivity.class);
			} else {
				intent.setClass(getActivity(), EmergeContactActivity.class);
			}
			intent.putExtra("role", "student");
			startActivity(intent);
		} else {
			sentHttpData(name);
			searchNewsList();

		}
	}

	// 查询并拨号
	@SuppressLint("NewApi")
	public void searchCallContact(String name) {

		intentCall = new Intent();
		data.clear();
		getFuzzyQueryByName(name);
		Log.e("mytag", "data.size====" + data.size());

		adapterCall = new SimpleAdapter(getActivity(), data,
				R.layout.call_listview_item, new String[] { "name", "phone" },
				new int[] { R.id.item, R.id.phone });

		listview.setAdapter(adapterCall);
		adapterCall.notifyDataSetChanged();
		if (data.size() >= 1) {
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					TextView phone = (TextView) arg1.findViewById(R.id.phone);
					String phonename = phone.getText().toString();
					Log.e("mytag", "phone=====" + phonename);
					intentCall.setAction("android.intent.action.CALL");
					intentCall.setData(Uri.parse("tel:" + phonename));
					startActivity(intentCall);
					adapterCall.notifyDataSetChanged();
					textContent.setText("");
				}
			});
		} else {
			Tools.mToast(getActivity(), R.string.voice_no_person);
		}
	}

	// 查询并发短信
	@SuppressLint("NewApi")
	public void searchMessageContact(String name) {
		intentMessage = new Intent();

		data.clear();
		getFuzzyQueryByName(name);
		Log.e("mytag", "data.size====" + data.size());

		adapterMessage = new SimpleAdapter(getActivity(), data,
				R.layout.call_listview_item, new String[] { "name", "phone" },
				new int[] { R.id.item, R.id.phone });

		listview.setAdapter(adapterMessage);
		adapterMessage.notifyDataSetChanged();
		if (data.size() >= 1) {
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					TextView phone = (TextView) arg1.findViewById(R.id.phone);
					String phonename = phone.getText().toString();
					Log.e("mytag", "phone=====" + phonename);
					Uri smsToUri = Uri.parse("smsto:");
					Intent sendIntent = new Intent(Intent.ACTION_VIEW, smsToUri);
					sendIntent.putExtra("address", phonename); // 电话号码，这行去掉的话，默认就没有电话
					sendIntent.setType("vnd.android-dir/mms-sms");
					startActivity(sendIntent);
					adapterMessage.notifyDataSetChanged();
				}
			});
		} else {
			Toast.makeText(getActivity(), "没有找到此人", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 查看新闻
	 */
	public void searchNewsList() {

		SimpleAdapter adapter = new SimpleAdapter(getActivity(), list,
				android.R.layout.simple_list_item_2, new String[] { "Theme",
						"Url" }, new int[] { android.R.id.text1,
						android.R.id.text2 });
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		if (list.size() >= 1) {
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long arg3) {
					// TODO Auto-generated method stub
					HashMap<String, Object> map = (HashMap<String, Object>) parent
							.getItemAtPosition(position);
					Intent intent = new Intent();
					intent.setClass(VoiceFragment.this.getActivity(),
							NewsWebViewActivity.class);
					intent.putExtra("Url", map.get("Url").toString());
					startActivity(intent);
				}
			});
		} else {
			Toast.makeText(getActivity(), "没有搜索到此新闻", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 根据名字中的某一个字进行模糊查询
	 * 
	 * @param key
	 */
	@SuppressLint("NewApi")
	private void getFuzzyQueryByName(String key) {

		EmergencyHelper emergeHelper = new EmergencyHelper(this.getActivity());
		StringBuilder sb = new StringBuilder();
		Cursor cursorLeader = Leader.getLeaderName(emergeHelper, key);
		Log.i("mytag", "cursorcall==" + cursorLeader.getCount());

		for (int i = 1; i <= cursorLeader.getCount(); i++) {
			Log.i("mytag",
					"name=="
							+ cursorLeader.getString(cursorLeader
									.getColumnIndex("leadername")));
			String name = cursorLeader.getString(cursorLeader
					.getColumnIndex("leadername"));
			String number = cursorLeader.getString(cursorLeader
					.getColumnIndex("leaderphone"));
			sb.append(name + " (").append(number + ")").append("\r\n");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("phone", number);
			data.add(map);
			cursorLeader.moveToNext();
		}

		Cursor cursorTeacher = HeadTeacher.getTeacherName(emergeHelper, key);
		Log.i("mytag", "cursorcall==" + cursorTeacher.getCount());

		for (int i = 1; i <= cursorTeacher.getCount(); i++) {
			Log.i("mytag",
					"name=="
							+ cursorTeacher.getString(cursorTeacher
									.getColumnIndex("tname")));
			String name = cursorTeacher.getString(cursorTeacher
					.getColumnIndex("tname"));
			String number = cursorTeacher.getString(cursorTeacher
					.getColumnIndex("tphone"));
			sb.append(name + " (").append(number + ")").append("\r\n");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("phone", number);
			data.add(map);
			cursorTeacher.moveToNext();
		}
		Cursor cursorStudent = ClassMates.getStudentName(emergeHelper, key);
		Log.i("mytag", "cursorcall==" + cursorStudent.getCount());

		for (int i = 1; i <= cursorStudent.getCount(); i++) {
			Log.i("mytag",
					"name=="
							+ cursorStudent.getString(cursorStudent
									.getColumnIndex("studentname")));
			String name = cursorStudent.getString(cursorStudent
					.getColumnIndex("studentname"));
			String number = cursorStudent.getString(cursorStudent
					.getColumnIndex("studentphone"));
			sb.append(name + " (").append(number + ")").append("\r\n");
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", name);
			map.put("phone", number);
			data.add(map);
			cursorStudent.moveToNext();
		}
	}

	public void sentHttpData(String text) {

		obj = new JSONObject();
		try {
			obj.put("newsSearch", text);
			Log.i("mytag", obj.toString());

			Thread threadEmergency = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();

					JSONObject jsonNewsResult = urlService
							.sentParams2DataServer(obj, ConstantData.CHECKNEWS);
					Log.i(TAG, "jsonNewsResult==" + jsonNewsResult);
					list.clear();
					try {
						for (int i = 0; i < jsonNewsResult.length(); i++) {
							Map<String, String> map = new HashMap<String, String>();
							String newsarray = jsonNewsResult.getString("" + i);
							JSONObject jsonnews = new JSONObject(newsarray);
							String theme = jsonnews.getString("newsTitle");
							String url = jsonnews.getString("url");

							Log.i(TAG, "theme==" + theme);
							Log.i(TAG, "url==" + url);

							map.put("Theme", theme);
							map.put("Url", url);
							list.add(map);
							Log.i(TAG, "list=" + list.toString());

						}

					} catch (JSONException e) {
						e.printStackTrace();
						handler.sendEmptyMessage(5);
						return;
					}
				}

			});
			threadEmergency.start();

		} catch (Exception e) {
			Tools.mToast(getActivity(), R.string.server_error);
			e.printStackTrace();
		}
	}

}
