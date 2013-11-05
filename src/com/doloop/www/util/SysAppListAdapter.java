package com.doloop.www.util;

import java.util.ArrayList;
import java.util.HashMap;

import com.doloop.www.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SysAppListAdapter extends SectionedBaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<String> sectionTextList;
	private HashMap<String, ArrayList<AppInfo>> sectionItemsMap;

	public SysAppListAdapter(Context context, ArrayList<String> sectionTxtList,
			HashMap<String, ArrayList<AppInfo>> secItemsMap) {
		this.mInflater = LayoutInflater.from(context);
		this.sectionTextList = sectionTxtList;
		this.sectionItemsMap = secItemsMap;
	}

	@Override
	public Object getItem(int section, int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int section, int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSectionCount() {
		// TODO Auto-generated method stub
		return sectionTextList.size();
	}

	@Override
	public int getCountForSection(int section) {
		// TODO Auto-generated method stub
		int val = sectionItemsMap.get(sectionTextList.get(section)).size();
		return val;
	}

	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		AppInfo appInfo = sectionItemsMap.get(sectionTextList.get(section)).get(position);

		AppViewHolder appViewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sys_app_info_item, null);
			appViewHolder = new AppViewHolder();

			appViewHolder.AppNameTextView = (TextView) convertView.findViewById(R.id.app_name);
			appViewHolder.AppVersionTextView = (TextView) convertView.findViewById(R.id.app_version);
			appViewHolder.AppIconImageView = (ImageView) convertView.findViewById(R.id.app_icon);

			convertView.setTag(appViewHolder);
			
		} else {
			appViewHolder = (AppViewHolder) convertView.getTag();
		}
		//很重要的信息保存，保存是那个section，什么 position
		convertView.setContentDescription(section+"-"+position);
		
		appViewHolder.AppNameTextView.setText(appInfo.appName);
		appViewHolder.AppVersionTextView.setText("v" + appInfo.versionName + " | "+ appInfo.appSize);
		appViewHolder.AppIconImageView.setImageDrawable(appInfo.appIcon);

		return convertView;
	}

	@Override
	public View getSectionHeaderView(int section, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		SectionViewHolder sectionViewHolder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.sys_app_header_item, null);
			convertView.setClickable(false);
			sectionViewHolder = new SectionViewHolder();
			sectionViewHolder.sectionTextView = (TextView) convertView.findViewById(R.id.textItem);
			convertView.setTag(sectionViewHolder);
		} else {
			sectionViewHolder = (SectionViewHolder) convertView.getTag();
		}
		sectionViewHolder.sectionTextView.setText(sectionTextList.get(section));
		convertView.setContentDescription(""+section);
		return convertView;

	}

	private class SectionViewHolder
	{
		TextView sectionTextView;
	}
	
	private class AppViewHolder {
		TextView AppNameTextView;
		TextView AppVersionTextView;
		ImageView AppIconImageView;
	}

}
