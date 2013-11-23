package com.doloop.www.util;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppInfo {
	public String appName = "";
	public String appNamePinyin = "";
	public String appSortName = "";
	public String packageName = "";
	public String versionName = "";
	public int versionCode = 0;
	public Drawable appIcon = null;
	public String appSizeStr = "";
	public long appRawSize = -1;
	public String firstTimeInstallDate = "";
	public String lastModifiedTimeStr = "";
	public String apkFilePath = "";
	public long lastModifiedRawTime = -1;
	
	
	public void print() {
		Log.v("app", "Name:" + appName + " Package:" + packageName);
		Log.v("app", "Name:" + appName + " versionName:" + versionName);
		Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
	}

}