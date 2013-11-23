package com.doloop.www.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

public class Utilities {

	public static String getBackUpAPKfileDir(Context ctx)
	{
		String path = Environment.getExternalStorageDirectory().toString()+"/MyAppMgr/";
		File backUpFileDir = new File(path); 
		if(!backUpFileDir.exists())
		{
			backUpFileDir.mkdirs();
		}
		return path;
	}
	
	public static boolean copyFile(String srcFilePath, String destFilePath) {
		boolean success = false;

		File srcFile = new File(srcFilePath);
		File destFile = new File(destFilePath);
		
		try {
			FileInputStream fcin = new FileInputStream(srcFile);
	        FileOutputStream fcout = new FileOutputStream(destFile);
	        fcin.getChannel().transferTo(0, fcin.getChannel().size(), fcout.getChannel());  
	        fcin.close();  
	        fcout.close();  
	        success = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IOException e) {  
            e.printStackTrace();  
        } 

		return success;
	}

	public static boolean ContainsChinese(String str) {
		char[] ch = str.trim().toCharArray();
		for (int i = 0; i < ch.length; i++) {
			char c = ch[i];
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}

	// GENERAL_PUNCTUATION 判断中文的“号
	// CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
	// HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
	public static final boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param inputString
	 * @return 返回大写开头字母
	 */
	public static String GetFirstChar(String inputString) {
		String output = "";
		char[] input = inputString.trim().toCharArray();
		if (java.lang.Character.toString(input[0])
				.matches("[\\u4E00-\\u9FA5]+")) // 为汉字
		{
			HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
			format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			format.setVCharType(HanyuPinyinVCharType.WITH_V);
			try {
				String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[0],
						format);
				output += temp[0].charAt(0);
			} catch (BadHanyuPinyinOutputFormatCombination e) {
				e.printStackTrace();
			}
		} else
			output += java.lang.Character.toString(input[0]);

		return output.toUpperCase(Locale.getDefault());
	}

	/**
	 * 参考网站 http://hi.baidu.com/daqing15/item/613e59e0eb2424f32b09a413
	 * 
	 * @param inputString
	 *            可以是多种字符混合,包含汉字转成拼音，没有汉字返回 "" I love 北京-->I love beijing,
	 * @return
	 */
	public static void GetPingYin(AppInfo mAppInfo) {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_V);
		boolean ContainsChinese = false;
		mAppInfo.appSortName = "";
		mAppInfo.appNamePinyin = "";
		char[] input = mAppInfo.appName.trim().toCharArray();
		String output = "";

		try {
			for (int i = 0; i < input.length; i++) {
				if (java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")) // 为汉字
				{
					String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i], format);
					output += "z" + temp[0] + " ";// 说明英文字符是汉字转换的拼音，例如 英文a和啊计较
					ContainsChinese = true;
					mAppInfo.appNamePinyin += temp[0];
				} else
					output += java.lang.Character.toString(input[i]) + " ";
			}
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			e.printStackTrace();
		}
		
		if (ContainsChinese) {
			output = output.trim();
			mAppInfo.appSortName = output;
			//return output;
		} else {
			mAppInfo.appSortName = "";
			//return "";
		}
	}

	/**
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。 对于Android 2.3（Api Level
	 * 9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
	 * 
	 * @param context
	 * 
	 * @param packageName
	 *            应用程序的包名
	 */
	public static void showInstalledAppDetails(Context context,
			String packageName) {
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
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
					: APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME,
					APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		context.startActivity(intent);
	}

	public static ApplicationInfo getSelfAppInfo(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		ApplicationInfo appInfo = null;
		try {
			appInfo = pm.getApplicationInfo(ctx.getPackageName(), 0);
		} catch (final NameNotFoundException e) {
			appInfo = null;
		}

		return appInfo;
	}

	public static boolean isAnyStoreInstalled(Context ctx) {
		Intent market = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://search?q=dummy"));
		PackageManager manager = ctx.getPackageManager();
		List<ResolveInfo> list = manager.queryIntentActivities(market, 0);

		return list.size() > 0;
	}

	@SuppressLint("SimpleDateFormat")
	public static SimpleDateFormat getLocalDataDigitalDisplayFormat() {
		/**
		 * it dd/MM/yy ch yy-M-d eng M/d/yy
		 */
		SimpleDateFormat dateformat = new SimpleDateFormat(); // new
																// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datePatternStr = dateformat.toLocalizedPattern().split(" ")[0];
		String daySection = "";
		String MonthSection = "";
		String YearSection = "";
		for (int i = 0; i < datePatternStr.length(); i++) {
			if (datePatternStr.charAt(i) == 'd') {
				daySection += "d";
			} else if (datePatternStr.charAt(i) == 'M') {
				MonthSection += "M";
			} else if (datePatternStr.charAt(i) == 'y') {
				YearSection += "y";
			}
		}

		if (!daySection.equals("dd")) {
			datePatternStr = datePatternStr.replace(daySection, "dd");
		}

		if (!MonthSection.equals("MM")) {
			datePatternStr = datePatternStr.replace(MonthSection, "MM");
		}

		if (!YearSection.equals("yyyy")) {
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
					sub_string + 3) + " GB";
		} else if (length >= 1048576) {
			sub_string = String.valueOf((float) length / 1048576).indexOf(".");
			result = ((float) length / 1048576 + "000").substring(0,
					sub_string + 3) + " MB";
		} else if (length >= 1024) {
			sub_string = String.valueOf((float) length / 1024).indexOf(".");
			result = ((float) length / 1024 + "000").substring(0,
					sub_string + 3) + " KB";
		} else if (length < 1024)
			result = Long.toString(length) + " B";
		return result;
	}
}
