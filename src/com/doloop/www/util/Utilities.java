package com.doloop.www.util;

import java.text.SimpleDateFormat;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class Utilities {
	/**
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
	 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
	 * 
	 * @param context
	 * 
	 * @param packageName
	 *            应用程序的包名
	 */
	public static void showInstalledAppDetails(Context context, String packageName) {
		String SCHEME = "package";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
		 */
		String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
		/**
		 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
		 */
		String APP_PKG_NAME_22 = "pkg";
		/**
		 * InstalledAppDetails所在包名
		 */
		String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
		/**
		 * InstalledAppDetails类名
		 */
		String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
		
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22 : APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);
	}
	
	
	public static ApplicationInfo getSelfAppInfo(Context ctx)
	{
		 PackageManager pm = ctx.getPackageManager();
		 ApplicationInfo appInfo = null;
		 try {
			 appInfo = pm.getApplicationInfo(ctx.getPackageName(), 0);
		 } catch (final NameNotFoundException e) {
			 appInfo = null;
		 }
		 
		 return appInfo;
	}
	
	public static boolean isPlayStoreInstalled(Context ctx) {
        Intent market = new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=dummy"));
        PackageManager manager = ctx.getPackageManager();
        List<ResolveInfo> list = manager.queryIntentActivities(market, 0);

        return list.size() > 0;
    }
	
	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat getLocalDataDigitalDisplayFormat()
	{
		/**
		 * it dd/MM/yy
		   ch yy-M-d
		   eng M/d/yy
		 */
		SimpleDateFormat dateformat = new SimpleDateFormat(); //new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datePatternStr = dateformat.toLocalizedPattern().split(" ")[0];
		String daySection = "";
		String MonthSection = "";
		String YearSection = "";
		for(int i = 0;i<datePatternStr.length();i++)
		{
			if(datePatternStr.charAt(i) == 'd')
			{
				daySection += "d";
			}
			else if(datePatternStr.charAt(i) == 'M')
			{
				MonthSection += "M";
			}
			else if(datePatternStr.charAt(i) == 'y')
			{
				YearSection += "y";
			}
		}
		
		if(!daySection.equals("dd"))
		{
			datePatternStr = datePatternStr.replace(daySection, "dd");
		}
		
		if(!MonthSection.equals("MM"))
		{
			datePatternStr = datePatternStr.replace(MonthSection, "MM");
		}
		
		if(!YearSection.equals("yyyy"))
		{
			datePatternStr = datePatternStr.replace(YearSection, "yyyy");
		}
		
		dateformat.applyPattern(datePatternStr);
		
		return dateformat;
	}
	
	
	public static String formatFileSize(long length) {
		String result = null;
		int sub_string = 0;
		if (length >= 1073741824) {
			sub_string = String.valueOf((float) length / 1073741824).indexOf(
					".");
			result = ((float) length / 1073741824 + "000").substring(0,
					sub_string + 3)
					+ " GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3)
					+ " MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3)
					+ " KB";
		} else if (length < 1024)
			result = Long.toString(length) + " B";
		return result;
	}
}
