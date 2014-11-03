package com.beiyuan.appyujing.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;
import android.util.Log;

public class HttpUtils {
	public final static int METHOD_GET = 1;
	public final static int METHOD_POST = 2;

	public static HttpEntity getHttpEntity(String uri, int method,
			JSONObject json) {
		HttpEntity entity = null;
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpUriRequest req = null;
		switch (method) {
		case METHOD_GET:
			req = new HttpGet(uri);
			break;
		case METHOD_POST:
			try {
				Log.i("info", "post started");
				req = new HttpPost(uri);

				StringEntity se = new StringEntity(json.toString(), "UTF-8");
				se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				((HttpPost) req).setEntity(se);

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			break;
		}
		try {
			HttpResponse res = client.execute(req);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return entity = res.getEntity();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}

	public static HttpEntity getHttpEntity(String uri, int method) {
		HttpEntity entity = null;
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpUriRequest req = null;
		switch (method) {
		case METHOD_GET:
			req = new HttpGet(uri);
			break;
		case METHOD_POST:
			try {
				Log.i("info", "post started");
				req = new HttpPost(uri);

			} catch (Exception e1) {
				e1.printStackTrace();
			}
			break;
		}
		try {
			HttpResponse res = client.execute(req);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				return entity = res.getEntity();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entity;
	}

	public static InputStream getInputStream(HttpEntity entity) {
		InputStream in = null;
		try {
			if (entity != null) {
				return in = entity.getContent();
			}
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return in;
	}
}
