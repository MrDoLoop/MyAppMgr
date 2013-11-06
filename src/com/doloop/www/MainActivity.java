package com.doloop.www;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.doloop.www.SysAppsTabFragment.OnSysAppListItemSelectedListener;
import com.doloop.www.UserAppsTabFragment.OnUserAppListItemActionClickListener;
import com.doloop.www.UserAppsTabFragment.OnUserAppListItemSelectedListener;
import com.doloop.www.util.AppInfo;
import com.doloop.www.util.AppNameComparator;
import com.doloop.www.util.Utilities;
import com.doloop.www.util.ViewPagerAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements
		OnSysAppListItemSelectedListener, OnUserAppListItemSelectedListener,
		OnUserAppListItemActionClickListener {

	private final static int USR_APPS_TAB_POS = 0;
	private final static int SYS_APPS_TAB_POS = 1;
	private final static int ALL_APPS_TAB_POS = 2;
	
	private ActionBar actionBar;
	private ViewPager viewPager;
	private static long back_pressed = 0;
	private Toast toast;
	private Context thisActivityCtx;

	private ArrayList<Fragment> Fragmentlist;

	private UserAppsTabFragment usrAppsFrg;
	private ArrayList<AppInfo> UserAppList = new ArrayList<AppInfo>();

	private SysAppsTabFragment sysAppsFrg;
	private ArrayList<AppInfo> SysAppList = new ArrayList<AppInfo>();
	private ArrayList<String> sectionTextList = new ArrayList<String>();
	private HashMap<String, ArrayList<AppInfo>> sectionItemsMap = new HashMap<String, ArrayList<AppInfo>>();

	private Fragment allAppsFrg;
	private ArrayList<AppInfo> AllAppList = new ArrayList<AppInfo>();

	private ProgressDialog progDialog;
	private SlidingMenu mSlidingMenu;

	private boolean isPlayStoreInstalled = false;
	
	private String switchCaseStr = "initDummy";
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		thisActivityCtx = MainActivity.this;
		isPlayStoreInstalled = Utilities.isPlayStoreInstalled(thisActivityCtx);
		actionBar = getSupportActionBar();
		actionBar.setSubtitle("NAN MADE");
		toast = Toast.makeText(thisActivityCtx, "", Toast.LENGTH_SHORT);

		Fragmentlist = new ArrayList<Fragment>();
		usrAppsFrg = new UserAppsTabFragment();
		sysAppsFrg = new SysAppsTabFragment();
		allAppsFrg = new AllAppsTabFragment();

		Fragmentlist.add(usrAppsFrg);
		Fragmentlist.add(sysAppsFrg);
		Fragmentlist.add(allAppsFrg);

		InitSlidingMenu(savedInstanceState);

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setOnPageChangeListener(onPageChangeListener);
		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),Fragmentlist));

		addActionBarTabs();
		new GetApps().execute();
	}

	private ViewPager.SimpleOnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			super.onPageSelected(position);
			switchCaseStr += "P";
			if(switchCaseStr.equals("TP"))//点击tab标签
			{
				switchCaseStr = "";
				return;
			}
			
			actionBar.setSelectedNavigationItem(position);
		}
	};

	private void InitSlidingMenu(Bundle savedInstanceState) {
		ListFragment mFrag;
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager()
					.beginTransaction();
			mFrag = new SampleListFragment();
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (ListFragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
		}

		// customize the SlidingMenu
		mSlidingMenu = getSlidingMenu();
		mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		mSlidingMenu.setShadowDrawable(R.drawable.shadow);
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		actionBar.setHomeButtonEnabled(true);// .setDisplayHomeAsUpEnabled(true);
		setSlidingActionBarEnabled(false);
	}

	private void addActionBarTabs() {
		// actionBar = getSupportActionBar();
		String[] tabs = { "USER APPs", "SYS APPs", "ALL APPs" };
		for (String tabTitle : tabs) {
			ActionBar.Tab tab = actionBar.newTab().setText(tabTitle)
					.setTabListener(tabListener);
			actionBar.addTab(tab);
		}
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	}

	private ActionBar.TabListener tabListener = new ActionBar.TabListener() {
		@Override
		public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
			if(switchCaseStr.equals("initDummy"))
			{
				switchCaseStr = "";
				return;
			}
			switchCaseStr += "T";
			if(switchCaseStr.equals("PT"))//滑动page
			{
				switchCaseStr = "";
			}
			
			int tabPos = tab.getPosition();
			viewPager.setCurrentItem(tabPos);
			if (mSlidingMenu.isMenuShowing()) {
				mSlidingMenu.showContent();
			}
			if (tabPos == 0) {
				mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
			} else {
				mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			}
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
			
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
			if (mSlidingMenu.isMenuShowing()) {
				mSlidingMenu.showContent();
			}
			
				switch (tab.getPosition()) 
				{
					case USR_APPS_TAB_POS:
						usrAppsFrg.getListView().smoothScrollToPosition(0);
						break;
					case SYS_APPS_TAB_POS:
						sysAppsFrg.getListView().smoothScrollToPosition(0);
						break;
					case ALL_APPS_TAB_POS:
	
						break;
				}
		}
	};

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// return true;
	// }
	//
	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// if(menuOnFirstTimeCreate)
	// {
	// menuOnFirstTimeCreate = false;
	// }
	// else
	// {
	// toggle();
	// }
	// return true;
	// }

	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			back_pressed = System.currentTimeMillis();
			toast.setText("press BACK again to exit");
			toast.show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class GetApps extends AsyncTask<Void, String, Void> {

		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			progDialog.setMessage("Loading Apps " + values[0]);
		}

		@Override
		protected void onPreExecute() {
			progDialog = new ProgressDialog(MainActivity.this);
			progDialog.setCancelable(false);
			progDialog.setMessage("Loading Apps");
			progDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					GetApps.this.cancel(true);
				}

			});
			progDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// getAppList();
			SysAppList.clear();
			UserAppList.clear();
			PackageManager pManager = getPackageManager();
			List<PackageInfo> packages = pManager.getInstalledPackages(0);

			PackageInfo packageInfo;
			AppInfo tmpInfo;
			// SimpleDateFormat dateformat = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (int i = 0; i < packages.size(); i++) {

				publishProgress((i + 1) + " / " + packages.size());

				packageInfo = packages.get(i);

				tmpInfo = new AppInfo();
				tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
						pManager).toString();
				tmpInfo.packageName = packageInfo.packageName;
				tmpInfo.versionName = packageInfo.versionName;
				tmpInfo.versionCode = packageInfo.versionCode;
				tmpInfo.appIcon = packageInfo.applicationInfo
						.loadIcon(pManager);
				// tmpInfo.firstTimeInstallDate =
				// dateformat.format(packageInfo.firstInstallTime);
				tmpInfo.appSize = Utilities.formatFileSize(
						new File(packageInfo.applicationInfo.publicSourceDir)
								.length()).toString();

				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					UserAppList.add(tmpInfo);// user app
				} else// sys app
				{
					SysAppList.add(tmpInfo);
				}
			}

			AppNameComparator nameComparator = new AppNameComparator();
			Collections.sort(UserAppList, nameComparator);
			Collections.sort(SysAppList, nameComparator);

			// build 系统applist
			AppInfo curAppInfo;
			AppInfo preAppInfo;
			String curSectionStr = "";
			ArrayList<AppInfo> sectionItemsTmp;
			for (int i = 0; i < SysAppList.size(); i++) {
				curAppInfo = SysAppList.get(i);
				curSectionStr = curAppInfo.appName.substring(0, 1).toUpperCase(
						Locale.getDefault());
				if (i == 0)// 第一个条目，直接添加一个section
				{
					sectionTextList.add(curSectionStr);
					sectionItemsTmp = new ArrayList<AppInfo>();
					sectionItemsTmp.add(curAppInfo);
					sectionItemsMap.put(curSectionStr, sectionItemsTmp);
				} else {
					preAppInfo = SysAppList.get(i - 1);
					if (curSectionStr.equalsIgnoreCase(preAppInfo.appName
							.subSequence(0, 1).toString())) {// 与前一个是同一个开始的字符
						sectionItemsMap.get(curSectionStr).add(curAppInfo);
					} else// 一个新section
					{
						sectionTextList.add(curSectionStr);
						sectionItemsTmp = new ArrayList<AppInfo>();
						sectionItemsTmp.add(curAppInfo);
						sectionItemsMap.put(curSectionStr, sectionItemsTmp);
					}
				}
			}

			return null;
		}

		@Override
		protected void onCancelled() {
			// MainActivity.this.finish();
		}

		// can use UI thread here
		@Override
		protected void onPostExecute(final Void unused) {
			if (progDialog.isShowing()) {
				progDialog.dismiss();
			}
			// 设置tab 标题
			actionBar.getTabAt(USR_APPS_TAB_POS).setText(
					"USER APPS (" + UserAppList.size() + ")");
			actionBar.getTabAt(SYS_APPS_TAB_POS).setText(
					"SYS APPS (" + SysAppList.size() + ")");
			actionBar.getTabAt(ALL_APPS_TAB_POS).setText(
					"ALL APPS (" + AllAppList.size() + ")");

			// list设置数据
			sysAppsFrg.setData(sectionTextList, sectionItemsMap);
			usrAppsFrg.setData(UserAppList);
			
//			if(!firstTimeLoadFinish)
//			{
//				firstTimeLoadFinish = true;
//			}
		}
	}

	// 系统app list点击事件
	@Override
	public void onSysAppItemClick(View v, int position) {
		// TODO Auto-generated method stub
		String viewContentDesStr = v.getContentDescription().toString();
		if (viewContentDesStr.contains("-"))// app view "section-position"
		{
			String[] DesStr = viewContentDesStr.split("-");
			String section = DesStr[0];
			int pos = Integer.parseInt(DesStr[1]);

			String toastMsg = "SYS appList app click\n"
					+ sectionItemsMap.get(
							sectionTextList.get(Integer.parseInt(section)))
							.get(pos).appName;
			toast.setText(toastMsg);
			toast.show();
		} else// section
		{
			// sysAppsFrg.getListView().smoothScrollToPosition(0);
			sysAppsFrg.getListView().setSelection(position);

			// toast.setText("SYS appList section click "+sectionTextList.get(Integer.parseInt(viewContentDesStr)));
			// toast.show();
		}
	}

	// 用户App列表点击事件
	@Override
	public void onUserAppItemClick(View v, int position) {
		// TODO Auto-generated method stub

		toast.setText("user AppList " + position);
		toast.show();
	}

	// 用户App列表中的action点击事件
	@Override
	public void onUserAppItemActionClick(View listView, View buttonview,
			int position) {
		// TODO Auto-generated method stub
		switch (buttonview.getId()) {
		case R.id.openBtn:
			Intent intent = getPackageManager().getLaunchIntentForPackage(
					UserAppList.get(position).packageName);
			if (intent != null) {
				startActivity(intent);
			} else {
				toast.setText("error");
				toast.show();
			}
			break;
		case R.id.GPBtn:
			if (isPlayStoreInstalled) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id="+ UserAppList.get(position).packageName)));
			} else {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ UserAppList.get(position).packageName)));
			}
			break;
		case R.id.UninstallBtn:
			Uri packageUri = Uri.parse("package:"
					+ UserAppList.get(position).packageName);
			Intent uninstallIntent;
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
			} else {
				uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE,packageUri);
			}
			startActivity(uninstallIntent);
			break;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
	}

}
