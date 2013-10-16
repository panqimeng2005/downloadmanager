package com.nubia.downloadmanager;

import android.util.Log;

class MyLog {
	
	private static final boolean DEBUG = true;
	
	private static final String TAG = "NubiaDownloadManager";
	
	public static void v(String msg) {
		if (DEBUG) Log.v(TAG, msg);
	}
	
	public static void i(String msg) {
		if (DEBUG) Log.i(TAG, msg);
	}
	
	public static void w(String msg) {
		if (DEBUG) Log.w(TAG, msg);
	}
	
	public static void e(String msg) {
		if (DEBUG) Log.e(TAG, msg);
	}
}
