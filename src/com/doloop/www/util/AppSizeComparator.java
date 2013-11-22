package com.doloop.www.util;

import java.util.Comparator;

public class AppSizeComparator implements Comparator<AppInfo>{
	private boolean mAsc = false;
	/***
	 * 
	 * @param Asc:true asc, false des
	 */
	public AppSizeComparator(boolean Asc)
	{
		this.mAsc = Asc; 
	}
	
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		// TODO Auto-generated method stub
		int diff = (int) (lhs.appRawSize - rhs.appRawSize);
		
		if(mAsc)
		{
			return diff;
		}
		else
		{
			return -diff;
		}
	}

}
