package com.nubia.downloadmanager.demo;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nubia.downloadmanager.DownloadListener;
import com.nubia.downloadmanager.DownloadRequest;
import com.nubia.downloadmanager.R;

public class StatusIndicateView extends RelativeLayout implements DownloadListener {
	
	private static final String TAG = StatusIndicateView.class.getSimpleName();
	
	private TextView mTitleView;
	private TextView mDescriptionView;
	private TextView mProgressTextView;
	private ProgressBar mProgressBar;
	
	
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DownloadRequest request = (DownloadRequest)msg.obj;
			Log.i(TAG,  "a handleMessage request=" + request);
			setViewStatus(request);
		}
	};
	
    public StatusIndicateView(Context context) {
        super(context);
        inflateView(context);
    }

    public StatusIndicateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflateView(context);
    }

    public StatusIndicateView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        inflateView(context);
    }
    
    private void inflateView(Context context) {
    	LayoutInflater.from(context).inflate(
                R.layout.status_indicate_layout, this, true);

    	mTitleView = (TextView) findViewById(R.id.title_textview);
    	mDescriptionView = (TextView) findViewById(R.id.description_textview);
    	mProgressTextView = (TextView) findViewById(R.id.progress_textview);
    	mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
    }
 
    public void setViewStatus(DownloadRequest request) {
    	
    	String src = request.getSrcUri();
    	mTitleView.setText(src.substring(src.lastIndexOf("/")));
    	mDescriptionView.setText(src);
    	Log.i(TAG, request.getDownloadSize() + "/" + request.getTotalSize());
    	mProgressTextView.setText(request.getDownloadSize()
    			+ "/" + request.getTotalSize());
 
    	mProgressBar.setMax((int)request.getTotalSize());
    	mProgressBar.setProgress((int)request.getDownloadSize());
    }
    
    @Override
	public void onStart(DownloadRequest request) {
		Log.v(TAG, " onStart() request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 1;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onProgress(DownloadRequest request) {
		Log.v(TAG, " onProgress()request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 2;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onError(DownloadRequest request) {
		Log.v(TAG,  " onError() request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 3;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onComplete(DownloadRequest request) {
		Log.v(TAG,  " onComplete() request" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 4;
		msg.obj = request;
		msg.sendToTarget();
	}
}
