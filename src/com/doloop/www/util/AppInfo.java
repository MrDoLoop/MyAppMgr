package com.doloop.www.util;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class AppInfo implements Parcelable {
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
	public long lastModifiedRawTime = -1;
	public String apkFilePath = "";
	public boolean selected = false;

	public void print() {
		Log.v("app", "Name:" + appName + " Package:" + packageName);
		Log.v("app", "Name:" + appName + " versionName:" + versionName);
		Log.v("app", "Name:" + appName + " versionCode:" + versionCode);
	}

	public String getBackupFileName_pkgName()
	{
		return packageName+"_v"+versionName+".apk";
	}
	
	public String getBackupFileName_AppName()
	{
		return appName+"_v"+versionName+".apk";
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(appName);
		dest.writeString(appNamePinyin);
		dest.writeString(appSortName);
		dest.writeString(packageName);
		dest.writeString(versionName);
		dest.writeInt(versionCode);
		//icon
		dest.writeString(appSizeStr);
		dest.writeLong(appRawSize);
		dest.writeString(firstTimeInstallDate);
		dest.writeString(lastModifiedTimeStr);
		dest.writeLong(lastModifiedRawTime);
		dest.writeString(apkFilePath);
		dest.writeBooleanArray(new boolean[] {selected});
	}

	public static final Parcelable.Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
		public AppInfo createFromParcel(Parcel source) {
			AppInfo mAppInfo = new AppInfo();
			mAppInfo.appName = source.readString();
			mAppInfo.appNamePinyin = source.readString();
			mAppInfo.appSortName = source.readString();
			mAppInfo.packageName = source.readString();
			mAppInfo.versionName = source.readString();
			mAppInfo.versionCode = source.readInt();
			//icon
			mAppInfo.appSizeStr = source.readString();
			mAppInfo.appRawSize = source.readLong();
			mAppInfo.firstTimeInstallDate = source.readString();
			mAppInfo.lastModifiedTimeStr = source.readString();
			mAppInfo.lastModifiedRawTime = source.readLong();
			mAppInfo.apkFilePath = source.readString();
			boolean[] myBooleanArr = new boolean[1];
			source.readBooleanArray(myBooleanArr);
			mAppInfo.selected = myBooleanArr[0];
			
			return mAppInfo;
		}

		public AppInfo[] newArray(int size) {
			return new AppInfo[size];
		}
	};

}