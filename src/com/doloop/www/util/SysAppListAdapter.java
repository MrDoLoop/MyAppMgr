package com.doloop.www.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.doloop.www.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

public class SysAppListAdapter extends SectionedBaseAdapter implements Filterable {

	private LayoutInflater mInflater;
	private ArrayList<String> full_sectionTextList;
	private ArrayList<String> sectionTextListDisplay;
	private HashMap<String, ArrayList<AppInfo>> full_sectionItemsMap;
	private HashMap<String, ArrayList<AppInfo>> sectionItemsMapDisplay;
	private SysAppFilter filter;

	public SysAppListFilterResultListener mFilterResultListener;

	// Container Activity must implement this interface
	public interface SysAppListFilterResultListener {
		public void onSysAppListFilterResultPublish(ArrayList<String> ResultSectionTextList,HashMap<String, ArrayList<AppInfo>> ResultSectionItemsMap);
	}
	
	public void setSysAppListFilterResultListener(SysAppListFilterResultListener sysAppListFilterResultListener)
	{
		this.mFilterResultListener = sysAppListFilterResultListener;
	}
	
	public SysAppListAdapter(Context context, ArrayList<String> sectionTxtList,
			HashMap<String, ArrayList<AppInfo>> secItemsMap) {
		this.mInflater = LayoutInflater.from(context);
		this.sectionTextListDisplay = this.full_sectionTextList = sectionTxtList;
		//this.sectionTextListDisplay = (ArrayList<String>)full_sectionTextList.clone();
		this.sectionItemsMapDisplay = this.full_sectionItemsMap = secItemsMap;
		//this.sectionItemsMapDisplay = (HashMap<String, ArrayList<AppInfo>>)full_sectionItemsMap.clone();
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
		return sectionTextListDisplay.size();
	}

	@Override
	public int getCountForSection(int section) {
		// TODO Auto-generated method stub
		int val = sectionItemsMapDisplay.get(sectionTextListDisplay.get(section)).size();
		return val;
	}

	/**
	 * 
	 * @return num of items in list without section
	 */
	public int getItemsCount()
	{
		int retVal = 0;
		Iterator<Entry<String, ArrayList<AppInfo>>> iter = sectionItemsMapDisplay.entrySet().iterator();
		while (iter.hasNext()) 
    	{
    		Map.Entry<String, ArrayList<AppInfo>> entry = (Map.Entry<String, ArrayList<AppInfo>>) iter.next();
			ArrayList<AppInfo> sectionItemsList = (ArrayList<AppInfo>)entry.getValue();
			retVal += sectionItemsList.size();
			
    	}
		return retVal;
	}
	
	/**
	 * 
	 * @param sectionTxt
	 * @return -1: not found, >-1: position
	 */
	public int getSectionPostionInList(String sectionTxt)
	{
		int retVal = 0;
		for(int i = 0;i<sectionTextListDisplay.size();i++)
		{
			if(sectionTextListDisplay.get(i).equals(sectionTxt))
			{
				return retVal+i;
			}
			else
			{
				retVal += sectionItemsMapDisplay.get(sectionTextListDisplay.get(i)).size();
			}
		}
		return -1;
	}
	
	@Override
	public View getItemView(int section, int position, View convertView,
			ViewGroup parent) {
		// TODO Auto-generated method stub
		AppInfo appInfo = sectionItemsMapDisplay.get(sectionTextListDisplay.get(section)).get(position);

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
		sectionViewHolder.sectionTextView.setText(sectionTextListDisplay.get(section));
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

	 private class SysAppFilter extends Filter 
	 {
		 @Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// TODO Auto-generated method stub
				//存储过滤的值
				sectionTextListDisplay = full_sectionTextList;
				sectionItemsMapDisplay = full_sectionItemsMap;
				
		        if(!TextUtils.isEmpty(constraint))
		        {	        	
		        	ArrayList<String> filteredSectionTextList = new ArrayList<String>();
		        	HashMap<String, ArrayList<AppInfo>> filteredSectionItemsMap = new HashMap<String, ArrayList<AppInfo>>();
		        	
		        	//items
		        	Iterator<Entry<String, ArrayList<AppInfo>>> iter = full_sectionItemsMap.entrySet().iterator();
		        	while (iter.hasNext()) 
		        	{
		        		Map.Entry<String, ArrayList<AppInfo>> entry = (Map.Entry<String, ArrayList<AppInfo>>) iter.next();
		        		String sectionkey = (String)entry.getKey();
		        		
						ArrayList<AppInfo> val = (ArrayList<AppInfo>)entry.getValue();
						ArrayList<AppInfo> tmpItemsInSection = new ArrayList<AppInfo>();
						for(int i = 0;i<val.size();i++)
						{
							if(val.get(i).appName.toLowerCase(Locale.getDefault()).contains(constraint))
							{
								tmpItemsInSection.add(val.get(i));
							}
						}
						if(tmpItemsInSection.size()>0)
						{
							filteredSectionTextList.add(sectionkey);
							filteredSectionItemsMap.put(sectionkey, tmpItemsInSection);
						}
		        	}	
		        	
		        	Collections.sort(filteredSectionTextList,new StringComparator());
		        	
		        	sectionTextListDisplay = filteredSectionTextList;
		        	sectionItemsMapDisplay = filteredSectionItemsMap;
		        }
		        
		        return null;
				
			}

			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// TODO Auto-generated method stub
				if(mFilterResultListener!=null)
				{
					mFilterResultListener.onSysAppListFilterResultPublish(sectionTextListDisplay,sectionItemsMapDisplay);
				}
				notifyDataSetChanged();
			}
	    	
	    }
	
	
	
	@Override
	public Filter getFilter() {
		// TODO Auto-generated method stub
		if (filter == null) {    
            filter = new SysAppFilter();    
        }    
        return filter;  
	}

}
