package com.beiyuan.appyujing.activity;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.beiyuan.appyujing.MyActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.adapter.StudentDetailAdapter;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.StudentData;
import com.beiyuan.appyujing.service.UrlService;
import com.beiyuan.appyujing.service.UrlServiceImpl;
import com.beiyuan.appyujing.tools.JsonParser;
import com.beiyuan.appyujing.tools.Tools;

/**
 * 快速查询查看学生的详细信息
 * 
 * @author juan
 * 
 */
public class StudentDetailActivity extends MyActivity {

	private String studentNumber;
	private String studentName;
	private ListView student_status_list;
	private final static String TAG = "StudentDetailActivity";
	private Handler handler;
	private String studentDetail;
	private List<StudentData> studentStatus;
	private ImageView backBtn;
	private ImageView getPhotoBtn;
	private TextView getClassMsg;
	private TextView stuName;
	private TextView stuId;
	private ProgressDialog pdDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_layout);
		studentNumber = getIntent().getStringExtra("studentnumber");
		studentName = getIntent().getStringExtra("studentname");
		initView();
		setListener();
		getStudentData();

		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					getClassMsg.setText(studentDetail);
					stuId.setText(studentNumber);
					StudentDetailAdapter adapter = new StudentDetailAdapter(
							StudentDetailActivity.this, studentStatus);
					student_status_list.setAdapter(adapter);
					pdDetail.dismiss();
					break;
				}
			}
		};
	}

	public void initView() {
		backBtn = (ImageView) findViewById(R.id.back);
		getPhotoBtn = (ImageView) findViewById(R.id.get_head_img);
		getClassMsg = (TextView) findViewById(R.id.get_class_name);
		stuName = (TextView) findViewById(R.id.get_name);
		stuId = (TextView) findViewById(R.id.get_stu_num);
		student_status_list = (ListView) findViewById(R.id.list_view);
		stuName.setText(studentName);
	}

	private void setListener() {
		// TODO Auto-generated method stub
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	/**
	 * 
	 */
	public void getStudentData() {

		final UrlService service = new UrlServiceImpl();
		final JSONObject obj = new JSONObject();
		try {
			pdDetail = Tools.pd(StudentDetailActivity.this, "正在获取");
			obj.put("studentNumber", studentNumber);
			Thread threadStudent = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					Looper.prepare();
					JSONObject rst_data = service.sentParams2DataServer(obj,
							ConstantData.QUICKLEADERBYSTUNUM);
					try {
						String studentlist = rst_data.getString("grade");
						JSONObject studentInfo = new JSONObject(studentlist);
						studentDetail = JsonParser.parseStudentInfoResult(
								studentInfo, studentName);
						studentStatus = JsonParser
								.parseStudentStatusResult(rst_data);
						handler.sendEmptyMessage(1);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						pdDetail.dismiss();
					}

				}

			});
			threadStudent.start();
		} catch (JSONException e) {
			Tools.mToast(StudentDetailActivity.this, "连接服务器失败···");
			e.printStackTrace();
			pdDetail.dismiss();
		}

	}

}
