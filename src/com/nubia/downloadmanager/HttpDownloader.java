package com.nubia.downloadmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

class HttpDownloader {
	
	private static final String TEMP_SUFFIX = ".tmp";
	
	private List<DownloadListener> mDownloadListeners;
	

	public HttpDownloader() {
		mDownloadListeners = new ArrayList<DownloadListener>();
	}
	
	public int doDownload(DownloadRequest request) {
		int statusCode = 0;

		MyLog.v("HttpDownloader Download() srcUri=" + request.getSrcUri());

		HttpClient httpClient = getHttpClient();
		HttpGet get = new HttpGet(request.getSrcUri());
		File destFile = setupFile(request.getDestUri() + TEMP_SUFFIX);
		
		try {
			
			OutputStream os = null;
			
			// if support continue download and file exist, start continue download.
			boolean isContinueDownload = request.getSupportContinue() && destFile.exists()
					&& (request.getDownloadSize() == destFile.length())
					&& (request.getTotalSize() != 0); 
			
			MyLog.v("HttpDownloader doDownload() isContinueDownload=" + isContinueDownload);
			MyLog.v("HttpDownloader doDownload() request.getSupportContinue()=" + request.getSupportContinue());
			MyLog.v("HttpDownloader doDownload() destFile.exists()=" + destFile.exists());
			MyLog.v("HttpDownloader doDownload() request.getDownloadSize()=" + request.getDownloadSize());
			MyLog.v("HttpDownloader doDownload() destFile.length()=" + destFile.length());
			MyLog.v("HttpDownloader doDownload() request.getTotalSize()=" + request.getTotalSize());
			
			if (isContinueDownload) {
				get.addHeader("RANGE", "bytes=" + destFile.length() + "-");
				os = new FileOutputStream(destFile, true);
			} else {
				os = new FileOutputStream(destFile);
			}
			
			HttpResponse response = httpClient.execute(get);
			HttpEntity entry = response.getEntity();
			MyLog.v("HttpDownloader Download() long=" + entry.getContentLength());
			InputStream is = entry.getContent();
			
			// If it is continue download, do not write file size. Because 
			// the continue file size from server is less than file real size.
			if (!isContinueDownload) {
				request.setTotalSize(entry.getContentLength());
				request.setDownloadSize(0);
			}
			
			notifyStart(request);
			
			writeToFile(is, os, request);
			
			if (request.getTotalSize() == request.getDownloadSize()) {
				destFile.renameTo(new File(request.getDestUri()));
				notifyComplete(request);
			} else {
				notifyProgress(request);
			}
		} catch (ClientProtocolException e) {
			MyLog.e("HttpDownloader Download() ClientProtocolException");
			statusCode = ErrorCode.CLIENT_PROTOCOL_ERROR;
			notifyError(request);
		} catch (FileNotFoundException e) {
			MyLog.e("HttpDownloader Download() FileNotFoundException");
			statusCode = ErrorCode.FILE_NOT_FOUND_ERROR;
			notifyError(request);
		} catch (IOException e) {
			MyLog.e("HttpDownloader Download() IOException");
			statusCode = ErrorCode.IO_ERROR;
			notifyError(request);
		} catch (Exception e) {
			MyLog.e("HttpDownloader Download() Exception");
			statusCode = ErrorCode.UNKNOW_ERROR;
			notifyError(request);
		}
		return statusCode;
	}

	private long writeToFile(InputStream is, OutputStream os, DownloadRequest request)
			throws IOException {
		try {
			byte buffer[] = new byte[4 * 1024];
			int len = 0;

			while (request.getDownloadStatus().equals(DownloadColumns.STATUS_START)
					&& (len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
				MyLog.v("HttpDownloader writeToFile len = " + len);
				request.setDownloadSize(request.getDownloadSize() + len);
				notifyProgress(request);				
			}
			os.flush();

		} catch (IOException e) {
			throw e;
		} finally {
			if (os != null) {
				os.close();
			}
		}
		return request.getDownloadSize();
	}

	private static File setupFile(String destUri) {
		File outFile = new File(destUri);
		if (!outFile.getParentFile().exists()) {
			outFile.getParentFile().mkdirs();
		}
		return outFile;
	}

	private static HttpClient getHttpClient() {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		return httpClient;
	}

	synchronized void addDownloadListener(DownloadListener listener) {
		if (!mDownloadListeners.contains(listener)) {
			mDownloadListeners.add(listener);
		}
	}

	synchronized void removeDownloadListener(DownloadListener listener) {
		mDownloadListeners.remove(listener);
	}
	
	synchronized private void notifyStart(DownloadRequest request) {
		request.setDownloadStatus(DownloadColumns.STATUS_START);
		for (DownloadListener l : mDownloadListeners) {
			MyLog.v("HttpDownloader notifyStart() onStart() length = " + mDownloadListeners.size());
			l.onStart(request);
		}
		if (request.getDownloadListener() != null) {
			MyLog.v("HttpDownloader notifyStart() onStart() request");
			request.getDownloadListener().onStart(request);
		}
	}
	
	synchronized private void notifyComplete(DownloadRequest request) {
		request.setDownloadStatus(DownloadColumns.STATUS_COMPLETE);
		for (DownloadListener l : mDownloadListeners) {
			MyLog.v("HttpDownloader notifyComplete() onComplete() length = " + mDownloadListeners.size());
			l.onComplete(request);
		}
		if (request.getDownloadListener() != null) {
			MyLog.v("HttpDownloader notifyComplete() onComplete() request");
			request.getDownloadListener().onComplete(request);
		}
	}
	
	synchronized private void notifyProgress(DownloadRequest request) {
		for (DownloadListener l : mDownloadListeners) {
			MyLog.v("HttpDownloader notifyProgress() onProgress() length = " + mDownloadListeners.size());
			l.onProgress(request);
		}
		if (request.getDownloadListener() != null) {
			MyLog.v("HttpDownloader notifyStart() onProgress() request");
			request.getDownloadListener().onProgress(request);
		}
	}
	
	synchronized private void notifyError(DownloadRequest request) {
		request.setDownloadStatus(DownloadColumns.STATUS_ERROR);
		for (DownloadListener l : mDownloadListeners) {
			MyLog.v("HttpDownloader notifyError() onError() length = " + mDownloadListeners.size());
			l.onError(request);
		}
		if (request.getDownloadListener() != null) {
			MyLog.v("HttpDownloader notifyStart() onError() request");
			request.getDownloadListener().onError(request);
		}
	}
}
