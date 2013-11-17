package com.doloop.www;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.doloop.slideexpandable.library.ActionSlideExpandableListView;
import com.doloop.www.SampleListFragment.OnMenuListItemClickListener;
import com.doloop.www.SampleListFragment.SampleItem;
import com.doloop.www.SysAppsTabFragment.OnSysAppListItemSelectedListener;
import com.doloop.www.UserAppsTabFragment.OnUserAppListItemActionClickListener;
import com.doloop.www.UserAppsTabFragment.OnUserAppListItemSelectedListener;
import com.doloop.www.util.AppInfo;
import com.doloop.www.util.AppNameComparator;
import com.doloop.www.util.AppPinYinComparator;
import com.doloop.www.util.StringComparator;
import com.doloop.www.util.SysAppListAdapter;
import com.doloop.www.util.SysAppListAdapter.SysAppListFilterResultListener;
import com.doloop.www.util.UserAppListAdapter;
import com.doloop.www.util.UserAppListAdapter.UserAppListFilterResultListener;
import com.doloop.www.util.Utilities;
import com.doloop.www.util.ViewPagerAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity implements
		OnSysAppListItemSelectedListener, OnUserAppListItemSelectedListener,
		OnUserAppListItemActionClickListener,UserAppListFilterResultListener,
		SysAppListFilterResultListener,OnMenuListItemClickListener{

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

	private boolean isAnyStoreInstalled = false;
	
	private String switchCaseStr = "initDummy";
	
	private AppUpdateReceiver mAppUpdateReceiver;
	private IntentFilter AppIntentFilter;
	
	private LangUpdateReceiver LangUpdateReceiver;
	private IntentFilter LangIntentFilter;
	
	private String thisAppPackageName = "";//避免点击自己，启动自己

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		thisActivityCtx = MainActivity.this;
		isAnyStoreInstalled = Utilities.isAnyStoreInstalled(thisActivityCtx);
		thisAppPackageName = Utilities.getSelfAppInfo(thisActivityCtx).packageName;
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
		
		mAppUpdateReceiver = new AppUpdateReceiver();
		AppIntentFilter = new IntentFilter(); 
		AppIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED); 
		AppIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED); 
		AppIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
		AppIntentFilter.addDataScheme("package");
		registerReceiver(mAppUpdateReceiver, AppIntentFilter); 
		
		LangIntentFilter = new IntentFilter(); 
		LangIntentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
		
		LangUpdateReceiver = new LangUpdateReceiver();
		registerReceiver(LangUpdateReceiver , LangIntentFilter);
		
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
		SampleListFragment mFrag;
		setBehindContentView(R.layout.menu_frame);
		if (savedInstanceState == null) {
			FragmentTransaction t = this.getSupportFragmentManager()
					.beginTransaction();
			mFrag = new SampleListFragment();
			mFrag.setOnMenuListItemClickListener(this);
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (SampleListFragment) this.getSupportFragmentManager()
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
			else
			{
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
		}
	};

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	 if(keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0){  
             toggle();
             return true;
         }
		return super.onKeyDown(keyCode, event);
	}
	
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
//		 MenuItem item = menu.add("Search");
//         item.setIcon(android.R.drawable.ic_menu_search);
//         item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//         View searchView = SearchViewCompat.newSearchView(thisActivityCtx);
//         if (searchView != null) {
//        	 SearchViewCompat.setSubmitButtonEnabled(searchView, false);
//        	 SearchViewCompat.setQueryHint(searchView, "key words");
//             SearchViewCompat.setOnQueryTextListener(searchView,
//                     new OnQueryTextListenerCompat() {
//                 @Override
//                 public boolean onQueryTextChange(String newText) {
//                     // Called when the action bar search text has changed.  Since this
//                     // is a simple array adapter, we can just have it do the filtering.
//                     String mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
//                     //mAdapter.getFilter().filter(mCurFilter);
//                     switch (actionBar.getSelectedTab().getPosition()) 
//      				 {
//      				 	case USR_APPS_TAB_POS:
//      				 		((UserAppListAdapter)usrAppsFrg.getListAdapter()).getFilter().filter(mCurFilter);
//      						break;
//      					case SYS_APPS_TAB_POS:
//      						((SysAppListAdapter)sysAppsFrg.getListAdapter()).getFilter().filter(mCurFilter);
//      						break;
//      					case ALL_APPS_TAB_POS:
//      	
//      						break;
//      				 }
//                     
//                     return true;
//                 }
//             });
//             item.setActionView(searchView);
//         }
		 
		 
		 SearchView searchView = new SearchView(actionBar.getThemedContext());
		 searchView.setQueryHint("key words");
		 searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				switch (actionBar.getSelectedTab().getPosition()) 
				{
	            	case USR_APPS_TAB_POS:
	              		((UserAppListAdapter)usrAppsFrg.getListAdapter()).getFilter().filter(query);
						break;
					case SYS_APPS_TAB_POS:
						((SysAppListAdapter)sysAppsFrg.getListAdapter()).getFilter().filter(query);
						break;
					case ALL_APPS_TAB_POS:
		
						break;
				}
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
              String mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
              //mAdapter.getFilter().filter(mCurFilter);
              switch (actionBar.getSelectedTab().getPosition()) 
              {
              	case USR_APPS_TAB_POS:
              		((UserAppListAdapter)usrAppsFrg.getListAdapter()).getFilter().filter(mCurFilter);
					break;
				case SYS_APPS_TAB_POS:
					((SysAppListAdapter)sysAppsFrg.getListAdapter()).getFilter().filter(mCurFilter);
					break;
				case ALL_APPS_TAB_POS:
	
					break;
				}
				return true;
			}});
		
		 MenuItem searchMenuItem = menu.add("Search");
		 searchMenuItem.setIcon(R.drawable.abs__ic_search);
		 searchMenuItem.setActionView(searchView);
		 searchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		 searchMenuItem.setOnActionExpandListener(new OnActionExpandListener(){

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				// TODO Auto-generated method stub
				((UserAppListAdapter)usrAppsFrg.getListAdapter()).getFilter().filter(null);
				((SysAppListAdapter)sysAppsFrg.getListAdapter()).getFilter().filter(null);
				return true;
			}});
		 return true;
	 }
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
			unregisterReceiver(mAppUpdateReceiver);
			unregisterReceiver(LangUpdateReceiver);
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
			//用于显示用户app的list
			UserAppList.clear();
			//用户显示系统app的list
			SysAppList.clear();
			sectionTextList.clear();
			sectionItemsMap.clear();
			
			PackageManager pManager = getPackageManager();
			List<PackageInfo> packages = pManager.getInstalledPackages(0);
			
//			List<ApplicationInfo> apps = pManager.getInstalledApplications(
//                    PackageManager.GET_UNINSTALLED_PACKAGES |
//                    PackageManager.GET_DISABLED_COMPONENTS);
			
			PackageInfo packageInfo;
			AppInfo tmpInfo;
			File tmpAPKfile;
			SimpleDateFormat simpleDateFormat = Utilities.getLocalDataDigitalDisplayFormat();
			for (int i = 0; i < packages.size(); i++) {

				publishProgress((i + 1) + " / " + packages.size());

				packageInfo = packages.get(i);

				tmpInfo = new AppInfo();
				tmpInfo.appName = packageInfo.applicationInfo.loadLabel(
						pManager).toString();
				tmpInfo.packageName = packageInfo.packageName;
				tmpInfo.versionName = packageInfo.versionName;
				tmpInfo.versionCode = packageInfo.versionCode;
				tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(pManager);
				// tmpInfo.firstTimeInstallDate =
				// dateformat.format(packageInfo.firstInstallTime);
				tmpAPKfile = new File(packageInfo.applicationInfo.publicSourceDir);
				tmpInfo.appSize = Utilities.formatFileSize(tmpAPKfile.length()).toString();
				tmpInfo.lastModifiedTime = simpleDateFormat.format(new Date(tmpAPKfile.lastModified()));
				//排序做处理
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {	
					UserAppList.add(tmpInfo);// user app
				} 
				else// sys app
				{
					tmpInfo.appNamePinyin = Utilities.GetPingYin(tmpInfo.appName);
					SysAppList.add(tmpInfo);
				}
			}
			
			//用户程序排序
			//Collections.sort(UserAppList, new AppPinYinComparator());
			Collections.sort(UserAppList, new AppNameComparator());//系统自带，默认的string排序
			// build 系统applist
			AppInfo curAppInfo;
			String curSectionStr = "";
			ArrayList<AppInfo> sectionItemsTmp;
			for (int i = 0; i < SysAppList.size(); i++) {
				curAppInfo = SysAppList.get(i);
				//curSectionStr = curAppInfo.appName.substring(0, 1).toUpperCase(Locale.getDefault());
				curSectionStr = Utilities.GetFirstChar(curAppInfo.appName);//.substring(0, 1).toUpperCase(Locale.getDefault());
				if(!Character.isLetter(curSectionStr.charAt(0)))//其他的开始的字母，放入#未分类
				{
					curSectionStr = "#";
				}
				if(sectionItemsMap.get(curSectionStr) == null)
				{
					sectionItemsTmp = new ArrayList<AppInfo>();
					sectionItemsTmp.add(curAppInfo);
					sectionItemsMap.put(curSectionStr, sectionItemsTmp);
					sectionTextList.add(curSectionStr);
				}
				else
				{
					sectionItemsMap.get(curSectionStr).add(curAppInfo);
				}
			}
			//排序整理
			Collections.sort(sectionTextList, new StringComparator());
			//确保"#"在最后
			if(sectionTextList.contains("#"))
			{
				sectionTextList.remove("#");
				sectionTextList.add("#");
			}
			AppPinYinComparator mAppPinYinComparator = new AppPinYinComparator();
			Iterator<Entry<String, ArrayList<AppInfo>>> iter = sectionItemsMap.entrySet().iterator();
			while (iter.hasNext()) 
			{
				Map.Entry<String, ArrayList<AppInfo>> entry = (Map.Entry<String, ArrayList<AppInfo>>) iter.next();
				ArrayList<AppInfo> sectionItemsList = (ArrayList<AppInfo>)entry.getValue();
				Collections.sort(sectionItemsList, mAppPinYinComparator);
			}
			
			
			return null;
		}

		@Override
		protected void onCancelled() {}

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
			
			registerReceiver(mAppUpdateReceiver, AppIntentFilter);
			registerReceiver(LangUpdateReceiver , LangIntentFilter);
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
		} 
		else// section
		{
			sysAppsFrg.getListView().setSelection(position);
		}
	}

	// 用户App列表点击事件
	@Override
	public void onUserAppItemClick(View v, int position) {
		// TODO Auto-generated method stub
		
		((ActionSlideExpandableListView)usrAppsFrg.getListView()).collapse(true);
		
		toast.setText("user AppList " + position);
		toast.show();
		
	}

	// 用户App列表中的action点击事件
	@Override
	public void onUserAppItemActionClick(View listView, View buttonview,
			int position) {
		// TODO Auto-generated method stub
		String targetpackageName = ((UserAppListAdapter)usrAppsFrg.getListAdapter()).getItem(position).packageName;
		switch (buttonview.getId()) {
		case R.id.openBtn:
			if(thisAppPackageName.equals(targetpackageName))//避免再次启动自己app
			{
				toast.setText("You catch me!!NAN Made app");
				toast.show();
			} 
			else
			{
				Intent intent = getPackageManager().getLaunchIntentForPackage(targetpackageName);
				if (intent != null) {
					startActivity(intent);
				} else {
					toast.setText("error");
					toast.show();
				}
			}
			break;
		case R.id.GPBtn:
			if (isAnyStoreInstalled) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id="+ targetpackageName)));
			} else {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ targetpackageName)));
			}
			break;
		case R.id.AppDetailsBtn:
			Utilities.showInstalledAppDetails(thisActivityCtx, targetpackageName);
			break;
		case R.id.UninstallBtn:
			Uri packageUri = Uri.parse("package:"
					+ targetpackageName);
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

	//用户app list过滤之后
	@Override
	public void onUserAppListFilterResultPublish(ArrayList<AppInfo> resultsList) {
		// TODO Auto-generated method stub
		((ActionSlideExpandableListView)usrAppsFrg.getListView()).collapse(false);
		String newTitle = "USER APPS (" + resultsList.size() + ")";
		actionBar.getTabAt(USR_APPS_TAB_POS).setText(newTitle);
	}
	
	@Override
	public void onSysAppListFilterResultPublish(
			ArrayList<String> ResultSectionTextList,
			HashMap<String, ArrayList<AppInfo>> ResultSectionItemsMap) {
		// TODO Auto-generated method stub
		sectionTextList = ResultSectionTextList;
		sectionItemsMap = ResultSectionItemsMap;
		
		String newTitle = "SYS APPS (" + ((SysAppListAdapter)sysAppsFrg.getListAdapter()).getItemsCount() + ")";
		actionBar.getTabAt(SYS_APPS_TAB_POS).setText(newTitle);
	}
	
	//菜单点击事件
	@Override
	public void OnMenuListItemClick(ListView MenulistView, View v, int position, long id) {
		// TODO Auto-generated method stub
		SampleItem MenuItem = (SampleItem)MenulistView.getAdapter().getItem(position);
		mSlidingMenu.showContent();
		toast.setText(MenuItem.tag);
		toast.show();
	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {

		}
	}
	
	@Override
	protected void onDestroy() {
	  super.onDestroy();
	  unregisterReceiver(mAppUpdateReceiver);
	  unregisterReceiver(LangUpdateReceiver);
	}
	
	private class LangUpdateReceiver extends BroadcastReceiver 
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(intent.getAction().compareTo(Intent.ACTION_LOCALE_CHANGED) == 0)
			{
				finish();
			}
		}
		
	}
	
	private class AppUpdateReceiver extends BroadcastReceiver 
	{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			 if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) 
			 {
				 String NewPkgName = intent.getDataString().substring(8);
				
				 PackageManager pm = getApplicationContext().getPackageManager();
				 ApplicationInfo ai = null;
				 try {
				     ai = pm.getApplicationInfo(NewPkgName, 0);
				 } catch (final NameNotFoundException e) {
				     ai = null;
				 }
				 String NewAppName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
				 toast.setText("New App Installed: "+ NewAppName);
				 toast.show();
				 new GetApps().execute();
			 }
			 else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) 
			 {
				 //String RemovedPkgName = intent.getDataString().substring(8); 
				 toast.setText("App Removed.");
				 toast.show();
				 new GetApps().execute();
			 }
			 else if(intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) 
			 {
				 new GetApps().execute();
			 }
		} 
	}
}
