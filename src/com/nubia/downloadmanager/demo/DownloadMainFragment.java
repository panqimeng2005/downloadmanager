package com.nubia.downloadmanager.demo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nubia.downloadmanager.DownloadManager;
import com.nubia.downloadmanager.DownloadRequest;
import com.nubia.downloadmanager.R;

public class DownloadMainFragment extends Fragment {
	
	private static final String TAG = DownloadMainFragment.class.getSimpleName();
	
	/********************************************* 
	 * Input your valid SD card folder path here *
	 *********************************************/
	private String destUri = 
			Environment.getExternalStorageDirectory().getPath()
			+ File.separator + "NuibaDownload"
			+ File.separator + "demo"
			+ File.separator;
	
	/********************************************* 
	 * Input your valid download addresses here  *
	 *********************************************/
	private String srcUris[] = {
			"http://show.v5.ztemt.com.cn/upload/pic/1743541109.jpg",
			"http://show.v5.ztemt.com.cn/upload/1500426928.jpg",
			"http://show.v5.ztemt.com.cn/upload/1500422368.jpg",
			"http://show.v5.ztemt.com.cn/upload/1500428132.jpg",
			"http://show.v5.ztemt.com.cn/upload/1148097853.jpg",
			"http://show.v5.ztemt.com.cn/upload/1042469850.jpg",
			"http://show.v5.ztemt.com.cn/upload/1042462875.jpg",
			"http://show.v5.ztemt.com.cn/upload/1030275406.jpg",
			};
	
	
	private DownloadManager mDownloadManager;

	private ItemListAdapter mListAdapter;
	private ListView mListView;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.download_main_fragment, null);
		initDownloadManager();
		initView(layout);
		return layout;
	}

	
	private void initDownloadManager() {
		mDownloadManager = ((MainActivity)getActivity()).getDownloadManager();
	}
	
	private void initView(View layout) {
		mListView = (ListView)layout.findViewById(R.id.list_view);
		
		ArrayList<DownloadRequest> requests = new ArrayList<DownloadRequest>();
		for (int i = 0; i < srcUris.length; ++i) {
			DownloadRequest request = new DownloadRequest(
					srcUris[i],
					destUri + srcUris[i].substring(srcUris[i].lastIndexOf("/")));
			requests.add(request);
		}
		
		mListAdapter = new ItemListAdapter(getActivity(), requests);
		mListView.setAdapter(mListAdapter);
	}

	
	private class ItemListAdapter extends ArrayAdapter<DownloadRequest> {
		
		public ItemListAdapter(Context context, List<DownloadRequest> list) {
			super(context, 0, list);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final DownloadRequest tag = getItem(position);
			
			if (convertView == null) {
				final LayoutInflater inflater = DownloadMainFragment
						.this.getActivity().getLayoutInflater();
				convertView = inflater.inflate(R.layout.download_main_item, parent, false);
			}
			
			TextView srcUriText = (TextView)convertView.findViewById(R.id.src_uri_textview);
			TextView destUriText = (TextView)convertView.findViewById(R.id.dest_uri_textview);
			Button startButton = (Button)convertView.findViewById(R.id.start_btn);
			
			srcUriText.setText(tag.getSrcUri());
			destUriText.setText(tag.getDestUri());
	
			startButton.setOnClickListener(new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					//String status = tag.getDownloadStatus();
					Log.w(TAG,  " onClick() begin status=" + tag.getDownloadStatus());
					//if (status.equals(DownloadColumns.STATUS_IDLE)) {
					mDownloadManager.enqueue(tag);
					//}
				}
			});
			
			return convertView;
		}
	}
}
