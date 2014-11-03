package com.beiyuan.appyujing.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Base64;

public class FileOperation {
	/*
	 * 将文件用Base64装换成String
	 */
	public static String File2String(String s) throws IOException {
		String ss = "";
		File picturefile = new File(Environment.getExternalStorageDirectory(),
				s);
		if (picturefile.exists()) {
			FileInputStream inputFile_pic = new FileInputStream(picturefile);
			byte[] buffer_pic = new byte[(int) picturefile.length()];
			inputFile_pic.read(buffer_pic);
			inputFile_pic.close();
			ss = Base64.encodeToString(buffer_pic, Base64.DEFAULT);
			return ss;
		} else {

			return ss;
		}

	}
}
