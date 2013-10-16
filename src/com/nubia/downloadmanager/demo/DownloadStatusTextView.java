package com.nubia.downloadmanager.demo;

import com.nubia.downloadmanager.DownloadListener;
import com.nubia.downloadmanager.DownloadRequest;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class DownloadStatusTextView extends TextView implements DownloadListener{
	private static final String TAG = DownloadStatusTextView.class.getSimpleName();
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DownloadRequest request = (DownloadRequest)msg.obj;
			Log.i(TAG,  "handleMessage request=" + request);
			setText(request);
		}
	};

	public DownloadStatusTextView(Context context) {
		super(context);
	}

	public DownloadStatusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public DownloadStatusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setText(DownloadRequest request) {
		setText(request.getDownloadSize() + "-" 
				+ request.getTotalSize() + "  "
				+ request.getDownloadStatus());
	}
	
	@Override
	public void onStart(DownloadRequest request) {
		Log.i(TAG, " onStart() request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 1;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onProgress(DownloadRequest request) {
		Log.i(TAG, " onProgress()request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 2;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onError(DownloadRequest request) {
		Log.i(TAG,  " onError() request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 3;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onComplete(DownloadRequest request) {
		Log.i(TAG,  " onComplete() request" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 4;
		msg.obj = request;
		msg.sendToTarget();
	}

}
