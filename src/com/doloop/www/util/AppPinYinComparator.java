package com.doloop.www.util;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public class AppPinYinComparator implements Comparator<AppInfo>{
	private final Collator sCollator = Collator.getInstance();
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		// TODO Auto-generated method stub	
		String str1 = "";
		String str2 = "";
//		if(lhs.appNamePinyin.length()>0 && rhs.appNamePinyin.length()>0)//�� : ��
//		{
//			str1 = lhs.appNamePinyin;
//			str2 = rhs.appNamePinyin;
//		}
//		else if(lhs.appNamePinyin.length()==0 && rhs.appNamePinyin.length()==0)//Ӣ : Ӣ
//		{
//			str1 = lhs.appName;
//			str2 = rhs.appName;
//		}
//		else if(lhs.appNamePinyin.length()==0 && rhs.appNamePinyin.length()>0)//Ӣ : ��
//		{
//			return -1;
//		}
//		else if(lhs.appNamePinyin.length()>0 && rhs.appNamePinyin.length()==0)//�� : Ӣ
//		{
//			return 1;
//		}
		
		str1 = lhs.appNamePinyin;
		str2 = rhs.appNamePinyin;
		return sCollator.compare(str1.toLowerCase(Locale.getDefault()), str2.toLowerCase(Locale.getDefault()));
	}

}
