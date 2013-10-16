package com.nubia.downloadmanager;

/**
 * The listener interface for download state change.
 * <p>
 * <strong>Note that the method will be invoked in new thread</strong>
 * 
 * @author Pan.Qimeng
 */
public interface DownloadListener {
	/**
	 * The method will be invoked when a request in the download waiting queue
	 * is start to download.
	 * 
	 * @param request
	 *            The download request object.
	 */
	void onStart(DownloadRequest request);

	/**
	 * The method will be invoked when a request download progress change.
	 * 
	 * @param request
	 *            The download request object.
	 */
	void onProgress(DownloadRequest request);

	/**
	 * The method will be invoked when a request download failed.
	 * 
	 * @param request
	 *            The download request object.
	 */
	void onError(DownloadRequest request);

	/**
	 * The method will be invoked when a request download complete. For example,
	 * You can update your application UI.
	 * 
	 * @param request
	 *            The download request object.
	 */
	void onComplete(DownloadRequest request);

}
