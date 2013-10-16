package com.nubia.downloadmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseHelper implements DownloadListener {
	
	private static final String DATABASE_NAME = "downloads.db";
	
	private static final String TABLE_NAME = "downloads";
	
	private static final int DATABASE_VERSION = 3;
	
	private List<ContentListener> mContentListeners;
	
	private MyOpenHelper mOpenHelper;
	
	public DatabaseHelper (Context context) {
		mOpenHelper = new MyOpenHelper(context);
		mContentListeners = new ArrayList<ContentListener>();
	}
	
	public long insert(ContentValues values) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long id = db.insert(TABLE_NAME, null, values);
		MyLog.v("DatabaseHelper insert() id=" + id);
		notifyContentChange();
		return id;
	}
	
	public long update(ContentValues values, final String where, final String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long id = db.update(TABLE_NAME, values, where, whereArgs);
		MyLog.v("DatabaseHelper update() id=" + id);
		notifyContentChange();
		return id;
	}
	
	public long delete(final String where, final String[] whereArgs) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		long id = db.delete(TABLE_NAME, where, whereArgs);
		MyLog.v("DatabaseHelper delete() id=" + id);
		notifyContentChange();
		return id;
	}
	
	public Cursor query(String[] projection, String selection,
			String[] selectionArgs, String orderBy) {
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		return db.query(TABLE_NAME, projection, selection,
				selectionArgs, null, null, orderBy);
	}
	
	private static final class MyOpenHelper extends SQLiteOpenHelper {

		public MyOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createTable(db);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			createTable(db);
		}

		private void createTable(SQLiteDatabase db) {
			StringBuilder sb = new StringBuilder();
			sb.append("CREATE TABLE ").append(TABLE_NAME).append("(")
					.append(DownloadColumns._ID).append(" INTEGER PRIMARY KEY autoincrement,")
					.append(DownloadColumns.UUID).append(" TEXT,")
					.append(DownloadColumns.SRC_URI).append(" TEXT,")
					.append(DownloadColumns.DEST_URI).append(" TEXT,")
					.append(DownloadColumns.TITLE).append(" TEXT,")
					.append(DownloadColumns.SUPPORT_CONTINUE).append(" TEXT,")
					.append(DownloadColumns.TOTAL_SIZE).append(" TEXT,")
					.append(DownloadColumns.DOWNLOAD_SIZE).append(" TEXT,")
					.append(DownloadColumns.DOWNLOAD_STATUS).append(" TEXT,")
					.append(DownloadColumns.TIMESTAMP).append(" TEXT,")
					.append(DownloadColumns.EXTRA_VALUE).append(" TEXT")
					.append(");");

			db.execSQL(sb.toString());
		}
	}
	
	
	public void addContentListener(ContentListener l) {
		if (!mContentListeners.contains(l)) {
			mContentListeners.add(l);
		}
	}

	public void removeContentListener(ContentListener l) {
		mContentListeners.remove(l);
	}
	
	public void notifyContentChange() {
		for (ContentListener l : mContentListeners) {
			l.onContentChange();
		}
	}

	@Override
	public void onStart(DownloadRequest request) {
		MyLog.i("DatabaseHelper onStart() request=" + request);
		String where = DownloadColumns._ID + "=" + request.getId();
		update(request.toContentValues(), where, null);
	}

	@Override
	public void onProgress(DownloadRequest request) {
		MyLog.i("DatabaseHelper onProgress() request=" + request);
		String where = DownloadColumns._ID + "=" + request.getId();
		update(request.toContentValues(), where, null);
	}

	@Override
	public void onError(DownloadRequest request) {
		MyLog.i("DatabaseHelper onError() request=" + request);
		String where = DownloadColumns._ID + "=" + request.getId();
		update(request.toContentValues(), where, null);
	}

	@Override
	public void onComplete(DownloadRequest request) {
		MyLog.i("DatabaseHelper onComplete() request=" + request);
		String where = DownloadColumns._ID + "=" + request.getId();
		update(request.toContentValues(), where, null);
	}
	
	/**
	 * The listener interface for database content change.
	 * 
	 * @author Pan.Qimeng
	 */
	public interface ContentListener {
		/**
		 * This method will be invoked, when database content change.
		 */
		void onContentChange();
	}
}
