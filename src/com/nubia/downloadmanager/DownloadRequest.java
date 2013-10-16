package com.nubia.downloadmanager;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * A download item, start or prepare a download task should pass this class object.
 * The object can be created by SrcUri/DestUri, or by a database entry.
 * 
 * @author Pan.Qimeng
 */
public class DownloadRequest {
	// if mId == -1, this request is a new request and need insert to database.
	private long mId = -1;
	private String mUuid = " ";
	private String mSrcUri;
	private String mDestUri;
	private boolean mSupportContinue = true;
	private String mTitle = "unknown";
	private long mTotalSize = 0;
	private long mDownloadSize = 0;
	private String mDownloadStatus = DownloadColumns.STATUS_IDLE;
	private String mTimeStamp = "00-00-00";
	private String mExtraValue = " ";
	
	private DownloadListener mDownloadListener;
	
	
	/**
	 * Create object from srcUri and destUri
	 * 
	 * @param srcUri  Source URI that download object is located.
	 * @param destUri  The target URI
	 */
	public DownloadRequest(String srcUri, String destUri) {
		mSrcUri = srcUri;
		mDestUri = destUri;
	}
	
	/**
	 * Create object from database entry.
	 * 
	 * @param cursor
	 */
	public DownloadRequest(Cursor cursor) {
		mId = cursor.getLong(cursor.getColumnIndex(DownloadColumns._ID));
		mUuid = cursor.getString(cursor.getColumnIndex(DownloadColumns.UUID));
		mSrcUri = cursor.getString(cursor.getColumnIndex(DownloadColumns.SRC_URI));
		mDestUri = cursor.getString(cursor.getColumnIndex(DownloadColumns.DEST_URI));
		mTitle = cursor.getString(cursor.getColumnIndex(DownloadColumns.TITLE));

		int intValue = cursor.getInt(cursor.getColumnIndex(DownloadColumns.SUPPORT_CONTINUE));
		mSupportContinue = (intValue == 0) ? false : true;
		
		mTotalSize = cursor.getLong(cursor.getColumnIndex(DownloadColumns.TOTAL_SIZE));
		mDownloadSize = cursor.getLong(cursor.getColumnIndex(DownloadColumns.DOWNLOAD_SIZE));
		mDownloadStatus = cursor.getString(cursor.getColumnIndex(DownloadColumns.DOWNLOAD_STATUS));
		mTimeStamp = cursor.getString(cursor.getColumnIndex(DownloadColumns.TIMESTAMP));
		mExtraValue = cursor.getString(cursor.getColumnIndex(DownloadColumns.EXTRA_VALUE));
	}
	
	/**
	 * Create database content values object by DownloadRequest.
	 * 
	 * @return  Database content values object.
	 */
	synchronized public ContentValues toContentValues() {
		ContentValues value = new ContentValues();
		if (mId != -1) {
			value.put(DownloadColumns._ID, mId);
		}
		value.put(DownloadColumns.UUID, mUuid);
		value.put(DownloadColumns.SRC_URI, mSrcUri);
		value.put(DownloadColumns.DEST_URI, mDestUri);
		value.put(DownloadColumns.TITLE, mTitle);
		value.put(DownloadColumns.SUPPORT_CONTINUE, (mSupportContinue ? 1 : 0));
		value.put(DownloadColumns.TOTAL_SIZE, mTotalSize);
		value.put(DownloadColumns.DOWNLOAD_SIZE, mDownloadSize);
		value.put(DownloadColumns.DOWNLOAD_STATUS, mDownloadStatus);
		value.put(DownloadColumns.TIMESTAMP, mTimeStamp);
		value.put(DownloadColumns.EXTRA_VALUE, mExtraValue);
		
		return value;
	}
	
	/**
	 * Get database primary key
	 * 
	 * @return  Database primary key.
	 */
	synchronized public long getId() {
		return mId;
	}
	
	/**
	 * Set database primary key. 
	 * <strong>Note, only in the package scope, it can not set by user.</strong>
	 * 
	 * @param id  Database primary key.
	 * @return  Database primary key.
	 */
	synchronized long setId(long id) {
		return mId = id;
	}
	
	synchronized public String getUuid() {
		return mUuid;
	}
	
	synchronized String setUuid(String uuid) {
		return mUuid = uuid;
	}
	
	synchronized public String getSrcUri() {
		return mSrcUri;
	}
	
	synchronized String setSrcUri(String srcUri) {
		return mSrcUri = srcUri;
	}
	
	synchronized public String getDestUri() {
		return mDestUri;
	}
	
	synchronized String setDestUri(String destUri) {
		return mDestUri = destUri;
	}
	
	synchronized public String getTitle() {
		return mTitle;
	}
	
	synchronized String setTitle(String title) {
		return mTitle = title;
	}
	
	synchronized public boolean getSupportContinue() {
		return mSupportContinue;
	}

	synchronized public void setSupportContinue(boolean isContinue) {
		mSupportContinue = isContinue;
	}
	
	synchronized public long getTotalSize() {
		return mTotalSize;
	}
	
	synchronized long setTotalSize(long size) {
		return mTotalSize = size;
	}
	
	synchronized public long getDownloadSize() {
		return mDownloadSize;
	}
	
	synchronized long setDownloadSize(long size) {
		return mDownloadSize = size;
	}

	synchronized public String getDownloadStatus() {
		return mDownloadStatus;
	}
	
	synchronized String setDownloadStatus(String status) {
		return mDownloadStatus = status;
	}
	
	synchronized public String getTimeStamp() {
		return mTimeStamp;
	}

	synchronized public void setTimeStamp(String timeStamp) {
		mTimeStamp = timeStamp;
	}
	
	synchronized public String getExtraValue() {
		return mExtraValue;
	}

	synchronized public void setExtraValue(String extraValue) {
		mExtraValue = extraValue;
	}
	
	synchronized public DownloadListener getDownloadListener() {
		return mDownloadListener;
	}
	
	synchronized public void setDownloadListener(DownloadListener listener) {
		mDownloadListener = listener;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[mId=").append(mId)
				.append(", mUuid=").append(mUuid)
				.append(", mSrcUri=").append(mSrcUri)
				.append(", mDestUri=").append(mDestUri)
				.append(", mTitle=").append(mTitle)
				.append(", mSupportContinue=").append(mSupportContinue)
				.append(", mTotalSize=").append(mTotalSize)
				.append(", mDownloadSize=").append(mDownloadSize)
				.append(", mDownloadStatus=").append(mDownloadStatus)
				.append(", mTimeStamp=").append(mTimeStamp)
				.append(", mExtraValue=").append(mExtraValue)
				.append("]");
		return sb.toString();
	}
}
