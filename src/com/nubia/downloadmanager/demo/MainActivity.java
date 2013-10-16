package com.nubia.downloadmanager.demo;

import com.nubia.downloadmanager.DownloadManager;
import com.nubia.downloadmanager.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;


public class MainActivity extends FragmentActivity {
	
	private ViewPager mViewPager;
	
	private MyViewPagerAdapter mAdapter;
	
	private DownloadManager mDownloadManager;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		initDownloadManager();
		initView();
	}
	
	public DownloadManager getDownloadManager() {
		if (mDownloadManager == null) {
			throw new IllegalStateException();
		}
		return mDownloadManager;
	}
	
	private void initDownloadManager() {
		mDownloadManager = 
				new DownloadManager.Builder()
				.setContext(this)
				.setMaxThread(3)
				.build();
		//mDownloadManager.addDownloadListener(this);
	}
	
	private void initView() {
		mViewPager = (ViewPager)findViewById(R.id.view_pager);
		
		mAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
		
		mViewPager.setAdapter(mAdapter);
	}

	private static class MyViewPagerAdapter extends FragmentPagerAdapter {

		MyViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public Fragment getItem(int index) {
			switch (index) {
			case 0:
				return new DownloadMainFragment();

			case 1:
				return new DownloadStatusFragment();
				
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return 2;
		}
		
	}
}
