package com.doloop.www.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class AppNameComparator implements Comparator<AppInfo>{
	private final Collator sCollator = Collator.getInstance();
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		// TODO Auto-generated method stub
		//return lhs.appName.toLowerCase().compareTo(rhs.appName.toLowerCase());
		
		return sCollator.compare(lhs.appName, rhs.appName.toLowerCase(Locale.getDefault()));
	}

}
