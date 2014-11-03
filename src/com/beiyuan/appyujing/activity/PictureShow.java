/**
 * 
 */
package com.beiyuan.appyujing.activity;

import com.beiyuan.appyujing.HeadteacherActivity;
import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.StudentActivity;
import com.beiyuan.appyujing.TeacherActivity;
import com.beiyuan.appyujing.data.ConstantData;
import com.beiyuan.appyujing.data.GlobalData;
import com.beiyuan.appyujing.service.AsyncImageLoader;
import com.beiyuan.appyujing.service.AsyncImageLoader.ImageCallback;
import com.beiyuan.appyujing.tools.BitmapUtil;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.DragImageView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
/**
 * @author kuoyu
 *
 */
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;

public class PictureShow extends Activity {
	private int window_width, window_height;// 控件宽度
	private DragImageView dragImageView;// 自定义控件
	private int state_height;// 状态栏的高度

	private ViewTreeObserver viewTreeObserver;
	Handler handler;
	Bitmap b = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.picture);
		/** 获取可見区域高度 **/

		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();
		handler = new Handler() {

			public void handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					dragImageView = (DragImageView) findViewById(R.id.picture_show);
					Bitmap bmp = BitmapUtil.ReadBitmapById(PictureShow.this, b,
							window_width, window_height);
					// 设置图片
					dragImageView.setImageBitmap(bmp);
					dragImageView.setmActivity(PictureShow.this);// 注入Activity.
					/** 测量状态栏高度 **/
					viewTreeObserver = dragImageView.getViewTreeObserver();
					viewTreeObserver
							.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

								@Override
								public void onGlobalLayout() {
									if (state_height == 0) {
										// 获取状况栏高度
										Rect frame = new Rect();
										getWindow().getDecorView()
												.getWindowVisibleDisplayFrame(
														frame);
										state_height = frame.top;
										dragImageView.setScreen_H(window_height
												- state_height);
										dragImageView.setScreen_W(window_width);
									}

								}
							});

					break;
				case -1:
					Tools.mToast(PictureShow.this, "读取图片失败");
					break;
				}
			}
		};

		Thread threadPicture = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				Intent intent = getIntent();
				final String URL = intent.getStringExtra("URL");
				Log.i("pic", URL);

				try {
					b = loadImageFromUrl(URL);
					Message message = new Message();
					message.what = 1;
					handler.sendMessage(message);
				} catch (Exception e) {
					e.printStackTrace();
					Message message = new Message();
					message.what = -1;
					handler.sendMessage(message);
				}
			}
		});
		threadPicture.start();

	}

	public Bitmap loadImageFromUrl(String url) throws Exception {
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(url);

		HttpResponse response = client.execute(getRequest);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != HttpStatus.SC_OK) {
			Log.e("PicShow", "Request URL failed, error code =" + statusCode);
		}

		HttpEntity entity = response.getEntity();
		if (entity == null) {
			Log.e("PicShow", "HttpEntity is null");
		}
		InputStream is = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			is = entity.getContent();
			byte[] buf = new byte[1024];
			int readBytes = -1;
			while ((readBytes = is.read(buf)) != -1) {
				baos.write(buf, 0, readBytes);
			}
		} finally {
			if (baos != null) {
				baos.close();
			}
			if (is != null) {
				is.close();
			}
		}
		byte[] imageArray = baos.toByteArray();
		return BitmapFactory.decodeByteArray(imageArray, 0, imageArray.length);
	}

	/**
	 * 读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

}