package com.doloop.www.util;

import java.util.ArrayList;
import com.doloop.www.R;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UserAppListAdapter extends ArrayAdapter<AppInfo>{
	
	private int ItemResourceLayout = 0;
	//private int textViewID = 0;
	private ArrayList<AppInfo> AppList;
	//private Context mContext;
	private LayoutInflater mInflater;
	
	public UserAppListAdapter(Context context, int resource,
			int textViewResourceId, ArrayList<AppInfo> appList) {
		super(context, resource, textViewResourceId, appList);
		// TODO Auto-generated constructor stub
		this.ItemResourceLayout = resource;
		//this.textViewID = textViewResourceId;
		this.AppList = appList;
		//this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return AppList.size();
	}

	@Override
	public AppInfo getItem(int position) {
		// TODO Auto-generated method stub
		return AppList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		AppInfo appInfo = AppList.get(position);
		
		ViewHolder holder;
		if(convertView==null)
  		{
  		//implement with XML layout file
			convertView = mInflater.inflate(ItemResourceLayout, null);//R.layout.app_info_item
  			holder = new ViewHolder();

			holder.AppNameTextView = (TextView) convertView.findViewById(R.id.app_name);
			holder.AppVersionTextView = (TextView) convertView.findViewById(R.id.app_version);
			holder.AppIconImageView = (ImageView) convertView.findViewById(R.id.app_icon);
			holder.moreItemBtn = (ImageButton) convertView.findViewById(R.id.expandable_toggle_button); 
			holder.moreItemBtn.setFocusable(false);
			holder.expandableLinearLayout = (LinearLayout) convertView.findViewById(R.id.expandable); 
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) 
			{
				holder.expandableLinearLayout.setPadding(1, 7, 1, 1);
			}
			convertView.setTag(holder);
  		}
  		else
  		{
  			holder = (ViewHolder)convertView.getTag();
  		}
		
		holder.AppNameTextView.setText(appInfo.appName);
		holder.AppVersionTextView.setText("v" + appInfo.versionName+" | "+appInfo.appSize);
		holder.AppIconImageView.setImageDrawable(appInfo.appIcon);
		
		return convertView;
	}
	
	
	
	private class ViewHolder
  	{
  		TextView AppNameTextView;
  		TextView AppVersionTextView;
  		ImageView AppIconImageView;
  		ImageButton moreItemBtn;
  		LinearLayout expandableLinearLayout;
  	}
}
