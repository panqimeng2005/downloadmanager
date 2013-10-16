package com.nubia.downloadmanager.demo;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nubia.downloadmanager.DownloadColumns;
import com.nubia.downloadmanager.DownloadListener;
import com.nubia.downloadmanager.DownloadManager;
import com.nubia.downloadmanager.DownloadRequest;
import com.nubia.downloadmanager.R;

public class DownloadStatusFragment extends Fragment implements DownloadListener{

	private static final String TAG = DownloadStatusFragment.class.getSimpleName();
	
	private DownloadManager mDownloadManager;
	
	private ItemListAdapter mListAdapter;
	private ListView mListView;
	
	private View mLayout;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			DownloadRequest request = (DownloadRequest)msg.obj;
			if (msg.what != 1) {
				updateItemView(request);
			} else {
				initView();
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mLayout = inflater.inflate(R.layout.download_status_fragment, null);
		initDownloadManager();
		initView();
		return mLayout;
	}

	
	private void initDownloadManager() {
		mDownloadManager = ((MainActivity)getActivity()).getDownloadManager();
		mDownloadManager.addDownloadListener(this);
	}
	
	private void initView() {
		mListView = (ListView)mLayout.findViewById(R.id.list_view);
		
		String selection = null;
//		DownloadColumns.DOWNLOAD_STATUS + 
//				"!='" + DownloadColumns.STATUS_COMPLETE + "'";
		List<DownloadRequest> requests = mDownloadManager.query(selection, null, null);
		Log.v(TAG, "refreshList requests length = " + requests.size());
		mListAdapter = new ItemListAdapter(getActivity(), requests);
		mListView.setAdapter(mListAdapter);
	}
	
	private void updateItemView(DownloadRequest request) {
		int visiblePos = mListAdapter.getRealPosition(request)
				- mListView.getFirstVisiblePosition();

		View listItemView = mListView.getChildAt(visiblePos);
		if (listItemView != null) {
			String info = request.getDownloadSize()/1000 + "k/" + request.getTotalSize()/1000
					+ "k-" + request.getDownloadStatus();
			TextView text = (TextView) listItemView.findViewById(R.id.progress_textview);
			Log.i(TAG, "mHandler : " + info);
			text.setText(info);
		}
	}
	
	class ViewHolder {
		TextView title;
		TextView description;
		TextView progress;
		Button deleteButton;
	}
	
	private class ItemListAdapter extends BaseAdapter {
		private List<DownloadRequest> mList;
				
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final DownloadRequest tag = (DownloadRequest) getItem(position);
			
			if (convertView == null) {
				final LayoutInflater inflater = DownloadStatusFragment
						.this.getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.download_status_item, parent, false);
				ViewHolder holder = new ViewHolder();
				holder.title = (TextView)convertView.findViewById(R.id.title_textview);
				holder.description = (TextView)convertView.findViewById(R.id.description_textview);
				holder.progress = (TextView)convertView.findViewById(R.id.progress_textview);
				holder.deleteButton = (Button)convertView.findViewById(R.id.delete_btn);
				convertView.setTag(holder);
			} 
			ViewHolder viewHolder = (ViewHolder) convertView.getTag();
			
	    	String src = tag.getSrcUri();
	    	viewHolder.title.setText(src.substring(src.lastIndexOf("/") + 1));
	    	viewHolder.description.setText(src);
	    	
	    	String info = tag.getDownloadSize()/1000 + "k/" + tag.getTotalSize()/1000
	    			+ "k-" + tag.getDownloadStatus();
	    	
	    	Log.i(TAG, "getView : " + info);
	    	viewHolder.progress.setText(info);
	    	
	    	viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mDownloadManager.delete(tag);
					initView();
				}
			});
	    	
	    	convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tag.getDownloadStatus().equals(DownloadColumns.STATUS_START)) {
						Log.i(TAG, "onClick pause status = " + tag.getDownloadStatus());
						mDownloadManager.pause(tag);
					} else if (!tag.getDownloadStatus().equals(DownloadColumns.STATUS_COMPLETE)){
						Log.i(TAG, "onClick enqueue status = " + tag.getDownloadStatus());
						mDownloadManager.enqueue(tag);
					}
				}
			});
	    	
			return convertView;
		}

		public ItemListAdapter(Context context, List<DownloadRequest> list) {
			mList = list;
		}
		
		public int getRealPosition(DownloadRequest request) {
			return mList.indexOf(request);
		}
		
		@Override
		public int getCount() {
			return mList.size();
		}
		
		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
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
		Log.v(TAG, " onProgress() request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 2;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onError(DownloadRequest request) {
		Log.v(TAG, " onError() request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 3;
		msg.obj = request;
		msg.sendToTarget();
	}

	@Override
	public void onComplete(DownloadRequest request) {
		Log.v(TAG, " onComplete() request=" + request);
		Message msg = mHandler.obtainMessage();
		msg.what = 4;
		msg.obj = request;
		msg.sendToTarget();
	}

}
