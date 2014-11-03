package com.beiyuan.appyujing.adapter;

import java.util.List;

import com.beiyuan.appyujing.R;
import com.beiyuan.appyujing.activity.PictureShow;
import com.beiyuan.appyujing.activity.WeiboActivity;
import com.beiyuan.appyujing.data.StudentInfo;
import com.beiyuan.appyujing.fragment.GaodeMapFragment;
import com.beiyuan.appyujing.service.AsyncImageLoader;
import com.beiyuan.appyujing.service.AsyncImageLoader.ImageCallback;
import com.beiyuan.appyujing.tools.Tools;
import com.beiyuan.appyujing.view.PullDownView;
import com.beiyuan.appyujing.view.ViewCache;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.Drawable;
import android.sax.StartElementListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class StrudentInfoListAdapter extends ArrayAdapter<StudentInfo> {

	private PullDownView listView;
	private AsyncImageLoader asyncImageLoader;
	Activity activity;
	List<StudentInfo> imageAndTexts;

	public StrudentInfoListAdapter(Activity activity,
			List<StudentInfo> imageAndTexts, PullDownView mPullDownView) {
		super(activity, 0, imageAndTexts);
		this.activity = activity;
		this.listView = mPullDownView;
		this.imageAndTexts = imageAndTexts;
		asyncImageLoader = new AsyncImageLoader();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final Activity activity = (Activity) getContext();

		// Inflate the views from XML
		View rowView = convertView;
		ViewCache viewCache;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.teacher_list, null);
			viewCache = new ViewCache(rowView);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) rowView.getTag();
		}
		final StudentInfo studentInfo = getItem(position);

		// Load the image and set it on the ImageView
		String touxiangURL = studentInfo.getThumbnailPictureURL();
		String pictureURL = studentInfo.getPictureURL();
		// ImageView touxiangView = viewCache.getView_touxiang();
		// ImageView pictureView = viewCache.getView_picture();
		ImageView view_touxiang = viewCache.getView_touxiang();
		TextView view_name = viewCache.getView_name();
		TextView view_time = viewCache.getView_time();
		TextView view_dl = viewCache.getView_dl();
		ImageView view_dw = viewCache.getView_dw();
		ImageView view_picture = viewCache.getView_picture();
		TextView view_word = viewCache.getView_word();
		TextView view_chatcontent = viewCache.getView_chatcontent();
		view_touxiang.setTag(touxiangURL);
		// Drawable cachedImage = asyncImageLoader.loadDrawable(touxiangURL,
		// new ImageCallback() {
		// public void imageLoaded(Drawable imageDrawable,
		// String imageUrl) {
		// ImageView imageViewByTag = (ImageView) listView
		// .findViewWithTag(imageUrl);
		// if (imageViewByTag != null) {
		// imageViewByTag.setImageDrawable(imageDrawable);
		// }
		// }
		// });
		// if (cachedImage == null) {
		view_touxiang.setImageResource(R.drawable.main_img);
		// } else {
		// view_touxiang.setImageDrawable(cachedImage);
		// }
		if (studentInfo.getPictureURL() != null) {
			view_word.setText(studentInfo.getWord());

			view_picture.setTag(pictureURL);
			Drawable cachedImage2 = asyncImageLoader.loadDrawable(pictureURL,
					new ImageCallback() {
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							ImageView imageViewByTag = (ImageView) listView
									.findViewWithTag(imageUrl);
							if (imageViewByTag != null) {
								imageViewByTag.setImageDrawable(imageDrawable);
							}
						}
					});
			if (cachedImage2 == null) {
				view_picture.setImageResource(R.drawable.main_img);
			} else {
				view_picture.setImageDrawable(cachedImage2);

			}
		}

		view_picture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Tools.mToast(activity,
				// "你点击了"+imageAndTexts.get(position).getTime()+"的图片");
				Intent intent = new Intent(activity, PictureShow.class);
				intent.putExtra("URL", imageAndTexts.get(position)
						.getThumbnailPictureURL());

				activity.startActivity(intent);
			}
		});
		// Set the text on the TextView
		view_name.setText(studentInfo.getStudentName());
		view_time.setText(studentInfo.getLongLatDate());
		if (studentInfo.getGeography() != null) {
			view_dl.setText(studentInfo.getGeography());
		}
		if (studentInfo.getGeography() != null) {
			view_dl.setText(studentInfo.getGeography());
		}
		if ((studentInfo.getLongitude() != null)
				&& (studentInfo.getLatitude() != null)) {
			view_dw.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// 定位
					//Intent intent = new Intent(activity, GaodeMapFragment.class);
					Intent data=new Intent();
					data.putExtra("lon", studentInfo.getLongitude());
					data.putExtra("lat", studentInfo.getLatitude());
					activity.setResult(200, data);
					activity.finish();
				}
			});
		}
		if (studentInfo.getWord() != null) {
			view_word.setText(studentInfo.getWord());
		}
		if (studentInfo.getWord() != null) {
			view_word.setText(studentInfo.getWord());
		}
		if (studentInfo.getSpeechURL() != null) {
			view_chatcontent.setVisibility(View.VISIBLE);
			view_chatcontent.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {

				}
			});
		}

		// view_word.setText(studentInfo.getWord());
		// view_chatcontent.setText(studentInfo.getThumbnailPictureURL());

		return rowView;
	}

	

}
