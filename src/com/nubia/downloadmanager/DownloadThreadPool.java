package com.nubia.downloadmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class DownloadThreadPool {

	private int mMaxThread;
	private ExecutorService mThreadPool;

	private HttpDownloader mHttpDownloader;
	
	private List<DownloadRequest> mDownloadRequests;
	
	DownloadThreadPool() {
		this(1);
	}

	DownloadThreadPool(int maxThread) {
		mMaxThread = maxThread;
		mThreadPool = Executors.newFixedThreadPool(mMaxThread);
		mHttpDownloader = new HttpDownloader();
		mDownloadRequests = new ArrayList<DownloadRequest>();
	}

	void enqueue(DownloadRequest request) {
		MyLog.v("DownloadThreadPool enqueue() request=" + request.toString());
		mThreadPool.submit(new DownloadTask(request));
		mDownloadRequests.add(request);
	}
	
	DownloadRequest getDownloadRequest(long id) {
		for (DownloadRequest r : mDownloadRequests) {
			if (r.getId() == id) {
				return r;
			}
		}
		return null;
	}
	
	class DownloadTask implements Runnable {

		private DownloadRequest mDownloadRequest;
		
		DownloadTask(DownloadRequest request) {
			mDownloadRequest = request;
		}
		
		@Override
		public void run() {
			MyLog.v("DownloadTask run() start");

			int statusCode = -1;
			if (mDownloadRequest.getDownloadStatus().equals(
					DownloadColumns.STATUS_IDLE)) {
				statusCode = mHttpDownloader.doDownload(mDownloadRequest);
			}
			mDownloadRequests.remove(mDownloadRequest);
			MyLog.v("DownloadTask run() end statusCode=" + statusCode);
		}
	}

	void addDownloadListener(DownloadListener l) {
		mHttpDownloader.addDownloadListener(l);
	}
	
	void removeDownloadListener(DownloadListener l) {
		mHttpDownloader.removeDownloadListener(l);
	}
}
