package com.doloop.www.util;

import android.graphics.drawable.Drawable;
import android.util.Log;

public class AppInfo {
	public String appName = "";
	public String appNamePinyin = "";
	public String packageName = "";
	public String versionName = "";
	public int versionCode = 0;
	public Drawable appIcon = null;
	public String appSize = "";
	public String firstTimeInstallDate = "";
	public String lastModifiedTime = "";
	public String ApkFilePath = "";
	
	
	public void print() {
		Log.v("app", "Name:" + appName + " Package:" + packageName);
		Log.v("app", "Name:" + appName + " versionName:" + versionName);
		Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
	}

}