package com.nubia.downloadmanager;

import android.provider.BaseColumns;

/**
 * Download database columns information.
 * 
 * @author Pan.Qimeng
 */
public final class DownloadColumns implements BaseColumns {
	/**
	 * The unique identifier for one database entry.
	 */
	public static final String UUID = "uuid";	
	/**
	 * The download source URI, it is the HTTP/HTTPS address.
	 */
	public static final String SRC_URI = "src_url";
	/**
	 * The download destination URI.
	 */
	public static final String DEST_URI = "dest_url";
	/**
	 * The file name, when save to the storage.
	 */
	public static final String TITLE = "title";
	/**
	 * If support continue download, 
	 * <p><strong>0 means not support continue download</strong>
	 */
	public static final String SUPPORT_CONTINUE = "support_continue";
	/**
	 * The file total size.
	 */
	public static final String TOTAL_SIZE = "total_size";
	/**
	 * The file already download size. 
	 * <p>For example, it can be used for pause and go on download 
	 */
	public static final String DOWNLOAD_SIZE = "download_size";
	/**
	 * Download status. such as IDLE, START, PAUSE, COMPLETE, ERROR.
	 */
	public static final String DOWNLOAD_STATUS = "status";
	/**
	 * The time stamp of download complete point.
	 */
	public static final String TIMESTAMP = "timestamp";
	/**
	 * Extra string value to store value.
	 */
	public static final String EXTRA_VALUE = "extra_value";
	
	/**
	 * Download status value, when download is idled.
	 */
	public static final String STATUS_IDLE = "status_idle";
	/**
	 * Download status value, when download is started.
	 */
	public static final String STATUS_START = "status_start";
	/**
	 * Download status value, when download is paused.
	 */
	public static final String STATUS_PAUSE = "status_pause";
	/**
	 * Download status value, when download is completed successfully.
	 */
	public static final String STATUS_COMPLETE = "status_complete";
	/**
	 * Download status value, when download has error.
	 */
	public static final String STATUS_ERROR = "status_error";
	/**
	 * Download status value, when download is paused.
	 */
	public static final String STATUS_DELETE = "status_delete";
	
}
