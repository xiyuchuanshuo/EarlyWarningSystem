package com.beiyuan.appyujing.view;

import com.beiyuan.appyujing.R;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewCache {

	private View baseView;
	ImageView view_touxiang;
	TextView view_name;
	TextView view_time;
	TextView view_dl;
	ImageView view_dw;
	ImageView view_picture;
	TextView view_word;
	TextView view_chatcontent;

	public ViewCache(View baseView) {
		this.baseView = baseView;
	}

	public ImageView getView_touxiang() {
		view_touxiang = (ImageView) baseView.findViewById(R.id.t_touxiang);
		return view_touxiang;
	}

	public TextView getView_name() {
		view_name = (TextView) baseView.findViewById(R.id.t_name);
		return view_name;
	}

	public TextView getView_time() {
		view_time = (TextView) baseView.findViewById(R.id.t_time);
		return view_time;
	}

	public TextView getView_dl() {
		view_dl = (TextView) baseView.findViewById(R.id.t_dl);
		return view_dl;
	}

	public ImageView getView_dw() {
		view_dw = (ImageView) baseView.findViewById(R.id.t_dw);
		return view_dw;
	}

	public ImageView getView_picture() {
		view_picture = (ImageView) baseView.findViewById(R.id.t_picture);
		return view_picture;
	}

	public TextView getView_word() {
		view_word = (TextView) baseView.findViewById(R.id.t_word);
		return view_word;
	}

	public TextView getView_chatcontent() {
		view_chatcontent = (TextView) baseView.findViewById(R.id.t_chatcontent);
		return view_chatcontent;
	}

}
