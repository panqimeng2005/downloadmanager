package com.nubia.downloadmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

/**
 * Download Manager single instance Class.
 * 
 * <p><strong>Note that it can only download by HTTP/HTTPS.</strong>
 * 
 * <p>All download tasks work in a thread pool. You have the ability
 * to set the max thread number when you create this object first
 * time, and the default value is one thread. 
 * 
 * <p>The download history will be recorded for your future query.
 * 
 * @author Pan.Qimeng
 * @version 1.0
 */
public class DownloadManager {
	private static final String TAG = DownloadManager.class.getSimpleName();
	
	private DownloadThreadPool mDownloadThreadPool;
	private DatabaseHelper mDBHelper;
	
	private static DownloadManager mInstance;
	
	/**
	 * The Download Builder class.
	 * <p><strong>Note must invoke {@link Builder#setContext(Context)} 
	 * before call {@link Builder#build()}</strong>
	 * 
	 * @author Pan.Qimeng
	 */
	public static class Builder {
		
		private Context mContext;
		private int mMaxThread = 1;
		
		/**
		 * Set application Context. 
		 * <p>if pass Activity context, it will 
		 *  
		 * @param context
		 * @return  Class object
		 */
		public Builder setContext(Context context) {
			mContext = context;
			return this;
		}
		/**
		 * Set max thread number.
		 * <p>recommend set max thread less than 3, when used in mobile.
		 * 
		 * @param maxThread  The max thread number, when downloading.
		 * @return  Class object
		 */
		public Builder setMaxThread(int maxThread) {
			mMaxThread = maxThread;
			return this;
		}
		/**
		 * Build a DownloadManager object from build parameter. 
		 * @return  DownloadManager object
		 */
		public DownloadManager build() {
			if (mContext == null) {
				throw new IllegalArgumentException();
			}
			return new DownloadManager(this);
		}
	}
	
	/**
	 * Get or create the DownloadManager single instance Object.
	 * 
	 * @param context
	 * @param maxThread  The max threads number to run the download task
	 * @return  Single instance object
	 * @deprecated  Use {@link Builder} to create DownloadManager object instead.
	 */
	public static DownloadManager getInstance(Context context, int maxThread) {
		synchronized (DownloadManager.class) {
			if (mInstance == null) {
				mInstance = new DownloadManager(
						context.getApplicationContext(), maxThread);
			}
		}
		return mInstance;
	}
	
	private DownloadManager(Context context, int maxThread) {
		if (mInstance != null) {
			throw new IllegalStateException("Can not create singlton object Duplicate");
		}
		mDownloadThreadPool = new DownloadThreadPool(maxThread);
		mDBHelper = new DatabaseHelper(context);
		addDownloadListener(mDBHelper);
	}
	
	private DownloadManager(Builder builder) {
		mDownloadThreadPool = new DownloadThreadPool(builder.mMaxThread);
		mDBHelper = new DatabaseHelper(builder.mContext);
		addDownloadListener(mDBHelper);
	}
	
	/**
	 * Add listeners for downloading status change
	 * 
	 * @param listener  listeners
	 */
	public void addDownloadListener(DownloadListener listener) {
		mDownloadThreadPool.addDownloadListener(listener);
	}
	
	/**
	 * Remove listeners for downloading status change
	 * 
	 * @param listener  listeners
	 */
	public void removeDownloadListener(DownloadListener listener) {
		mDownloadThreadPool.removeDownloadListener(listener);
	}
	
	/**
	 * Push a download task to the download thread pool queue.
	 * It will download immediately if queue has idle threads.
	 * 
	 * @param request  The download request object. 
	 * 			<p><strong>Note the request may be modify. such as when start download,
	 * 			the {@link DownloadRequest#mDownloadStatus} will modify </strong>
	 */
	public void enqueue(DownloadRequest request) {
		MyLog.v(TAG + " enqueue()" + request.toString());
		if (request.getId() == -1) {
			long id = mDBHelper.insert(request.toContentValues());
			request.setId(id);
		}
		request.setDownloadStatus(DownloadColumns.STATUS_IDLE);
		mDownloadThreadPool.enqueue(request);
	}
	
	/**
	 * Push a download tasks list to download thread pool queue.
	 * see {@link #enqueue(DownloadRequest)}
	 * <p><strong>Note that the requests.mDownloadStatus must be 
	 * equals to DownloadColumns.STATUS_IDLE</strong>
	 * 
	 * @param requests The request object
	 */
	public void enqueue(List<DownloadRequest> requests) {
		for (DownloadRequest r : requests) {
			enqueue(r);
		}
	}
	
	/**
	 * Pause a download task, if a task is downloading or ready to download.
	 * 
	 * @param request The request object
	 */
	public void pause(DownloadRequest request) {
		String status = request.getDownloadStatus();
		if (status.equals(DownloadColumns.STATUS_IDLE)
				|| status.equals(DownloadColumns.STATUS_START)) {
			request.setDownloadStatus(DownloadColumns.STATUS_PAUSE);
		}
	}
	
	/**
	 * Delete a download task, and delete the same entry from database.
	 * 
	 * @param request The request object
	 */
	public void delete(DownloadRequest request) {
		pause(request);
		String where = DownloadColumns._ID + "=" + request.getId();
		mDBHelper.delete(where, null);
	}
	
	/**
	 * Query the download request history in storage.
	 * 
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            Where clause, passing null will return all rows.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replace by the
	 *            values from this parameters, in the order that they appear in
	 *            the selection. The values will be bound as Strings
	 * @param orderBy
	 *            How to order the rows, formatted as and SQL ORDER BY clause.
	 *            Passing null will use the default sort order, which may be
	 *            unordered.
	 * @return The result set according to selection.
	 */
	public List<DownloadRequest> query(String selection, 
			String[] selectionArgs, String orderBy) {

		List<DownloadRequest> resultSet = new ArrayList<DownloadRequest>();
		Cursor cursor = mDBHelper.query(null, selection, selectionArgs, orderBy);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				long id = cursor.getLong(cursor.getColumnIndex(DownloadColumns._ID));
				DownloadRequest excutingRequest =
						mDownloadThreadPool.getDownloadRequest(id);
				if (excutingRequest != null) {
					MyLog.v(TAG + " query excutingRequest != null");
					resultSet.add(excutingRequest);
				} else {
					MyLog.v(TAG + " query excutingRequest == null");
					DownloadRequest request = new DownloadRequest(cursor);
					//request.setDownloadStatus(DownloadColumns.STATUS_IDLE);
					resultSet.add(request);
				}
			} while (cursor.moveToNext());
		}
		return resultSet;
	}
	
	/**
	 * Download all of not complete entries in download history database.
	 * 
	 * @deprecated  Please use query() and enqueue() to instead this method.
	 */
	public void enqueueHistory() {
		
		(new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				String selection = DownloadColumns.DOWNLOAD_STATUS + 
						"!='" + DownloadColumns.STATUS_COMPLETE + "'";
				Cursor cursor = mDBHelper.query(null, selection, null, null);
				MyLog.v(TAG + " enqueueHistory start run");
				if (cursor != null && cursor.moveToFirst()) {
					do {
						DownloadRequest request = new DownloadRequest(cursor);
						request.setDownloadStatus(DownloadColumns.STATUS_IDLE);
						MyLog.v(TAG + " enqueueHistory request=" + request);
						mDownloadThreadPool.enqueue(request);
					} while (cursor.moveToNext());
				}
			}
		})).start();
	}
}
