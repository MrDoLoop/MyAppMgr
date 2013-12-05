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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnActionExpandListener;
import com.actionbarsherlock.widget.SearchView;
import com.doloop.slideexpandable.library.ActionSlideExpandableListView;
import com.doloop.www.SampleListFragment.OnMenuListItemClickListener;
import com.doloop.www.SampleListFragment.SampleItem;
import com.doloop.www.SelectionDialogFragment.SelectionDialogClickListener;
import com.doloop.www.SortTypeDialogFragment.SortTypeListItemClickListener;
import com.doloop.www.SysAppsTabFragment.OnSysAppListItemSelectedListener;
import com.doloop.www.UserAppListMoreActionDialogFragment.UserAppMoreActionListItemClickListener;
import com.doloop.www.UserAppsTabFragment.OnUserAppListItemActionClickListener;
import com.doloop.www.UserAppsTabFragment.OnUserAppListItemLongClickListener;
import com.doloop.www.UserAppsTabFragment.OnUserAppListItemSelectedListener;
import com.doloop.www.util.AppInfo;
import com.doloop.www.util.AppNameComparator;
import com.doloop.www.util.AppPinYinComparator;
import com.doloop.www.util.AppSizeComparator;
import com.doloop.www.util.LastModifiedComparator;
import com.doloop.www.util.MyViewPager;
import com.doloop.www.util.StringComparator;
import com.doloop.www.util.SysAppListAdapter;
import com.doloop.www.util.SysAppListAdapter.SysAppListFilterResultListener;
import com.doloop.www.util.UserAppListAdapter;
import com.doloop.www.util.UserAppListAdapter.UserAppListFilterResultListener;
import com.doloop.www.util.Utilities;
import com.doloop.www.util.ViewPagerAdapter;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

@SuppressLint("InlinedApi")
@SuppressWarnings("deprecation")
public class MainActivity extends SlidingFragmentActivity implements
		OnSysAppListItemSelectedListener, OnUserAppListItemSelectedListener,
		OnUserAppListItemActionClickListener,UserAppListFilterResultListener,
		SysAppListFilterResultListener,OnMenuListItemClickListener, 
		OnUserAppListItemLongClickListener,SortTypeListItemClickListener,
		UserAppMoreActionListItemClickListener,SelectionDialogClickListener {

	private final static int USR_APPS_TAB_POS = 0;
	private final static int SYS_APPS_TAB_POS = 1;
	//private final static int ALL_APPS_TAB_POS = 2;
	
	private final static int SORT_MENU = Menu.FIRST;
	private final static int SEARCH_MENU = SORT_MENU + 1;
	
	private final static int ACTIONMODE_MENU_SELECT = 0;
	private final static int ACTIONMODE_MENU_BACKUP = 1;
	private final static int ACTIONMODE_MENU_SEND = 2;
	private final static int ACTIONMODE_MENU_UNINSTALL = 4;
	
	private ActionBar actionBar;
	private ActionMode mActionMode;
	private int UserAppActionModeSelectCnt = 0;
	
	private MyViewPager viewPager;
	private static long back_pressed = 0;
	private Toast toast;
	private Context thisActivityCtx;

	private ArrayList<Fragment> Fragmentlist;

	private UserAppsTabFragment usrAppsFrg;
	private UserAppListAdapter mUserAppListAdapter;
	private ArrayList<AppInfo> UserAppFullList = new ArrayList<AppInfo>();

	private SysAppsTabFragment sysAppsFrg;
	private SysAppListAdapter mSysAppListAdapter;
	private ArrayList<AppInfo> SysAppFullList = new ArrayList<AppInfo>();
	private ArrayList<String> sectionTextList = new ArrayList<String>();
	private HashMap<String, ArrayList<AppInfo>> sectionItemsMap = new HashMap<String, ArrayList<AppInfo>>();

//	private Fragment allAppsFrg;
//	private ArrayList<AppInfo> AllAppList = new ArrayList<AppInfo>();

	private ProgressDialog progDialog;
	private SlidingMenu mSlidingMenu;

	private boolean isAnyStoreInstalled = false;
	
	private String switchCaseStr = "initDummy";
	
	private AppUpdateReceiver mAppUpdateReceiver;
	private IntentFilter AppIntentFilter;
	
	private LangUpdateReceiver LangUpdateReceiver;
	private IntentFilter LangIntentFilter;
	
	private String thisAppPackageName = "";//避免点击自己，启动自己
	
	private String BACK_UP_FOLDER = "";
	
	private int screenWidth = 0;

	private MenuItem SortMenuItem = null;
	private MenuItem searchMenuItem = null ;
	
	private boolean SendAfterBackUp = false;
	
	private SortTypeDialogFragment SortTypeDialog;
	private UserAppListMoreActionDialogFragment UserAppListMoreActionDialog;
	private SelectionDialogFragment SelectionDialog;

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
		screenWidth  = getWindowManager().getDefaultDisplay().getWidth();
		
		InitSlidingMenu(savedInstanceState);
		
		addActionBarTabs();
		
		viewPager = (MyViewPager) findViewById(R.id.pager);
		viewPager.setOnPageChangeListener(onPageChangeListener);
		viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(),Fragmentlist));
		
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
		
		if(savedInstanceState != null)
		{
			FragmentManager mFragmentManager = getSupportFragmentManager();
			SortTypeDialog = (SortTypeDialogFragment) mFragmentManager.getFragment(savedInstanceState, SortTypeDialogFragment.DialogTag);
			UserAppListMoreActionDialog = (UserAppListMoreActionDialogFragment) mFragmentManager.getFragment(savedInstanceState, UserAppListMoreActionDialogFragment.DialogTag);
			SelectionDialog = (SelectionDialogFragment) mFragmentManager.getFragment(savedInstanceState, SelectionDialogFragment.DialogTag);
			
			if(SortTypeDialog != null)
				mFragmentManager.beginTransaction().remove(SortTypeDialog).commit();
			if(UserAppListMoreActionDialog != null)
				mFragmentManager.beginTransaction().remove(UserAppListMoreActionDialog).commit();
			if(SelectionDialog != null)
				mFragmentManager.beginTransaction().remove(SelectionDialog).commit();
		}
		
		new GetApps().execute();
	}
	
	
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) 
	{
	    if(ev.getAction() == MotionEvent.ACTION_DOWN) 
	    {
	        final View currentFocus = getCurrentFocus();
	        if (!(currentFocus instanceof AutoCompleteTextView) || !isTouchInsideView(ev, currentFocus)) {//AutoCompleteTextView
	            ((InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE))
	                .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	        }
	    }
		
		return super.dispatchTouchEvent(ev);
	}
	
	private boolean isTouchInsideView(final MotionEvent ev, final View currentFocus) {
	    final int[] loc = new int[2];
	    currentFocus.getLocationOnScreen(loc);
	    return ev.getRawX() > loc[0] && ev.getRawY() > loc[1] && ev.getRawX() < (loc[0] + screenWidth)//(loc[0] + currentFocus.getWidth())
	        && ev.getRawY() < (loc[1] + currentFocus.getHeight());
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
			t.replace(R.id.menu_frame, mFrag);
			t.commit();
		} else {
			mFrag = (SampleListFragment) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
		}
		
		mFrag.setOnMenuListItemClickListener(this);
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
		Fragmentlist = new ArrayList<Fragment>();
		usrAppsFrg = UserAppsTabFragment.getInstance(thisActivityCtx);
		sysAppsFrg = SysAppsTabFragment.getInstance(thisActivityCtx);
		//allAppsFrg = new AllAppsTabFragment();

		Fragmentlist.add(usrAppsFrg);
		Fragmentlist.add(sysAppsFrg);
		//Fragmentlist.add(allAppsFrg);
		
		String[] tabs = { getString(R.string.user_apps), getString(R.string.sys_apps)};//, "ALL APPs" 
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
			//在actionMode中什么都不做
			
			if(mActionMode != null)
			{
				if(tab.getPosition() != USR_APPS_TAB_POS)
				{
					actionBar.setSelectedNavigationItem(USR_APPS_TAB_POS);
				}
				return;
			}
			//
			//tab.setText(Html.fromHtml("<b><font color=#993399>你好吗，什么颜色</font></b>"));
			tab.setIcon(R.drawable.blue_ball);
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
			
			if(searchMenuItem != null && SortMenuItem != null)//防止恢复的情况
			{
				switch (tab.getPosition()) 
				{
					case USR_APPS_TAB_POS:
						if(!searchMenuItem.isActionViewExpanded())
							SortMenuItem.setVisible(true);
						break;
					case SYS_APPS_TAB_POS:
						SortMenuItem.setVisible(false);
						break;
				}
			}
			
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
//			if(tab.getPosition()==SYS_APPS_TAB_POS)
//			{
//				sysAppsFrg.ResetIndexBar();
//			}
			if(mActionMode == null)
			{
				tab.setIcon(null);
			}
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
//					case ALL_APPS_TAB_POS:
//	
//						break;
				}
			}
		}
	};

    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
    	//停止list滚动
//    	MotionEvent me = MotionEvent.obtain(SystemClock.uptimeMillis(),  SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0);
//    	sysAppsFrg.getListView().dispatchTouchEvent(me);
//    	me.recycle();
    	
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
		 searchView.setQueryHint(getString(R.string.key_words));
		 searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				onQueryTextChange(query);
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
              String mCurFilter = !TextUtils.isEmpty(newText) ? newText : null;
              switch (actionBar.getSelectedNavigationIndex()) 
              {
              	case USR_APPS_TAB_POS:
              		mUserAppListAdapter.getFilter().filter(mCurFilter);
					break;
				case SYS_APPS_TAB_POS:
					mSysAppListAdapter.getFilter().filter(mCurFilter);
					break;
//				case ALL_APPS_TAB_POS:
//	
//					break;
				}
				return true;
			}});

		 SortMenuItem = menu.add(Menu.NONE, SORT_MENU, Menu.NONE, R.string.sort);
		 switch (Utilities.getUserAppListSortType(thisActivityCtx))
		 {
			case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
				SortMenuItem.setIcon(R.drawable.name_asc);
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
				SortMenuItem.setIcon(R.drawable.name_des);
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
				SortMenuItem.setIcon(R.drawable.size_asc);
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
				SortMenuItem.setIcon(R.drawable.size_des);
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
				SortMenuItem.setIcon(R.drawable.time_asc);
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
				SortMenuItem.setIcon(R.drawable.time_des);
				break;
		}
		SortMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		searchMenuItem = menu.add(Menu.NONE, SEARCH_MENU, Menu.NONE, R.string.search);
		searchMenuItem.setIcon(R.drawable.ic_action_search);
		searchMenuItem.setActionView(searchView);
		searchMenuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		searchMenuItem.setOnActionExpandListener(new OnActionExpandListener(){

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// TODO Auto-generated method stub
				SortMenuItem.setVisible(false);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				// TODO Auto-generated method stub

				switch (actionBar.getSelectedNavigationIndex())
				{
				case USR_APPS_TAB_POS:
					SortMenuItem.setVisible(true);
					break;
				case SYS_APPS_TAB_POS:
					break;
				}
				
				mUserAppListAdapter.getFilter().filter(null);
				mSysAppListAdapter.getFilter().filter(null);
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
	 public boolean onSearchRequested() {  
	        // TODO Auto-generated method stub
		if(mActionMode == null)
		{
			if(searchMenuItem.isActionViewExpanded())
			{
				searchMenuItem.collapseActionView();
			}
			else
			{
				searchMenuItem.expandActionView();
			}
		}

		return false;  
	 }
	 
	@Override
	public void onBackPressed() {
		if (back_pressed + 2000 > System.currentTimeMillis()) {
			super.onBackPressed();
		} else {
			back_pressed = System.currentTimeMillis();
			toast.setText(R.string.press_back_again_to_exit);
			toast.show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		case SORT_MENU:
			
			SortTypeDialog = new SortTypeDialogFragment();
			Bundle bundle = new Bundle();
			bundle.putInt(SortTypeDialogFragment.SELECTED_ITEM, usrAppsFrg.getListSortType());
			SortTypeDialog.setArguments(bundle);
			SortTypeDialog.show(getSupportFragmentManager(), SortTypeDialogFragment.DialogTag);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private class BackUpApps extends AsyncTask<Void, String, Void> {
		boolean saveSucc = false;
		private ArrayList<Uri> SnedApkUris = new ArrayList<Uri>();
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			progDialog.setProgress(Integer.valueOf(values[0]));
			progDialog.setMessage(getString(R.string.saving_app)+" "+values[1]);
		}
		
		@Override
		protected void onPreExecute() {
			unregisterReceiver(mAppUpdateReceiver);
			unregisterReceiver(LangUpdateReceiver);
			progDialog = new ProgressDialog(thisActivityCtx);
			progDialog.setCancelable(false);
			progDialog.setMessage(getString(R.string.saving_apps));
			progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progDialog.setProgress(0);
			progDialog.setMax(UserAppActionModeSelectCnt);
			progDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					BackUpApps.this.cancel(true);
				}

			});
			progDialog.show();
		}
		
		
		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			BACK_UP_FOLDER = Utilities.getBackUpAPKfileDir(thisActivityCtx);
			String[] dialogInfo = new String[2];
			int counter = 1;
			AppInfo  tmpAppInfo = null;
			for(int i = 0;i<mUserAppListAdapter.getCount();i++)
			{
				tmpAppInfo = mUserAppListAdapter.getItem(i);
				if(tmpAppInfo.selected)
				{
					dialogInfo[0] = ""+counter;
					dialogInfo[1] = tmpAppInfo.appName;		
					publishProgress(dialogInfo);
							
					String backAPKfileName = tmpAppInfo.appName+"_v"+tmpAppInfo.versionName+".apk";
					if(!Utilities.copyFile(tmpAppInfo.apkFilePath,BACK_UP_FOLDER+backAPKfileName))
					{
						saveSucc = false;
						return null;
					}
							
					if(SendAfterBackUp)
					{
						SnedApkUris.add(Uri.parse("file://" + BACK_UP_FOLDER+backAPKfileName));
					}
							
					if(counter == UserAppActionModeSelectCnt)
					{
						break;
					}	
					counter++;
				}
			}		
							
			saveSucc = true;
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
			
			registerReceiver(mAppUpdateReceiver, AppIntentFilter);
			registerReceiver(LangUpdateReceiver , LangIntentFilter);
			
			if(saveSucc)
			{
				if(SendAfterBackUp)
				{
		            if(SnedApkUris.size() > 1)
		            {
		            	Utilities.chooseSendByApp(thisActivityCtx, SnedApkUris);
		            }
		            else
		            {
		            	Utilities.chooseSendByApp(thisActivityCtx, SnedApkUris.get(0));
		            }
				}
				else
				{
					toast.setText(R.string.backup_success);
					toast.show();
				}
			}
			else
			{
				toast.setText(R.string.error);
				toast.show();
				finish();
			}	
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		//DismissDialog(SortTypeDialog);
		//DismissDialog(UserAppListMoreActionDialog);
		super.onSaveInstanceState(savedInstanceState);
		
		if(SortTypeDialog != null && 
			SortTypeDialog.getDialog() != null && 
			SortTypeDialog.getDialog().isShowing())
		{
			getSupportFragmentManager().putFragment(savedInstanceState, SortTypeDialogFragment.DialogTag, SortTypeDialog);
		}
		if(UserAppListMoreActionDialog != null && 
			UserAppListMoreActionDialog.getDialog() != null && 
			UserAppListMoreActionDialog.getDialog().isShowing())
		{
			getSupportFragmentManager().putFragment(savedInstanceState, UserAppListMoreActionDialogFragment.DialogTag, UserAppListMoreActionDialog);
		}
		
		if(SelectionDialog != null && 
			SelectionDialog.getDialog() != null && 
			SelectionDialog.getDialog().isShowing())
		{
			getSupportFragmentManager().putFragment(savedInstanceState, SelectionDialogFragment.DialogTag, SelectionDialog);
		}
		
	}
	
	
	private class GetApps extends AsyncTask<Void, String, Void> {
		private List<PackageInfo> packages;
		private PackageManager pManager;
		@Override
		protected void onProgressUpdate(String... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			//progDialog.setMessage("Loading Apps " + values[0]);
			progDialog.setProgress(Integer.valueOf(values[0]));
		}

		@Override
		protected void onPreExecute() {
			unregisterReceiver(mAppUpdateReceiver);
			unregisterReceiver(LangUpdateReceiver);
			
			DismissDialog(SortTypeDialog);
			DismissDialog(UserAppListMoreActionDialog);
			DismissDialog(SelectionDialog);
			
			pManager = getPackageManager();
			packages = pManager.getInstalledPackages(0);
			
			progDialog = new ProgressDialog(MainActivity.this);
			progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progDialog.setProgress(0);
			progDialog.setMax(packages.size());
			progDialog.setCancelable(false);
			progDialog.setMessage(getString(R.string.loading_apps));
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
			UserAppFullList.clear();
			//用户显示系统app的list
			SysAppFullList.clear();
			sectionTextList.clear();
			sectionItemsMap.clear();
			
//			PackageManager pManager = getPackageManager();
//			List<PackageInfo> packages = pManager.getInstalledPackages(0);
			
//			List<ApplicationInfo> apps = pManager.getInstalledApplications(
//                    PackageManager.GET_UNINSTALLED_PACKAGES |
//                    PackageManager.GET_DISABLED_COMPONENTS);
			
			PackageInfo packageInfo;
			AppInfo tmpInfo;
			File tmpAPKfile;
			SimpleDateFormat simpleDateFormat = Utilities.getLocalDataDigitalDisplayFormat();
			for (int i = 0; i < packages.size(); i++) {

				//publishProgress((i + 1) + " / " + packages.size());
				publishProgress(""+(i+1));
				Log.i("ttt", "processing app "+(i + 1) + " / " + packages.size());
				packageInfo = packages.get(i);

				tmpInfo = new AppInfo();
				tmpInfo.appName = packageInfo.applicationInfo.loadLabel(pManager).toString();
				tmpInfo.packageName = packageInfo.packageName;
				tmpInfo.versionName = packageInfo.versionName;
				tmpInfo.versionCode = packageInfo.versionCode;
				tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(pManager);
				// tmpInfo.firstTimeInstallDate =
				// dateformat.format(packageInfo.firstInstallTime);
				tmpAPKfile = new File(packageInfo.applicationInfo.publicSourceDir);
				tmpInfo.appSizeStr = Utilities.formatFileSize(tmpAPKfile.length()).toString();
				tmpInfo.appRawSize = tmpAPKfile.length();
				tmpInfo.lastModifiedTimeStr = simpleDateFormat.format(new Date(tmpAPKfile.lastModified()));
				tmpInfo.lastModifiedRawTime = tmpAPKfile.lastModified();
				tmpInfo.apkFilePath = packageInfo.applicationInfo.publicSourceDir;
				Utilities.GetPingYin(tmpInfo);
				//排序做处理
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {	
					UserAppFullList.add(tmpInfo);// user app
				} 
				else// sys app
				{
					SysAppFullList.add(tmpInfo);
				}
				
			}
			
			//用户程序排序
			switch (Utilities.getUserAppListSortType(thisActivityCtx))
			{
			case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
				Collections.sort(UserAppFullList, new AppNameComparator(true));
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
				Collections.sort(UserAppFullList, new AppNameComparator(false));
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
				Collections.sort(UserAppFullList, new AppSizeComparator(true));
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
				Collections.sort(UserAppFullList, new AppSizeComparator(false));
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
				Collections.sort(UserAppFullList, new LastModifiedComparator(true));
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
				Collections.sort(UserAppFullList, new LastModifiedComparator(false));
				break;
			}
			
			// build 系统applist
			AppInfo curAppInfo;
			String curSectionStr = "";
			ArrayList<AppInfo> sectionItemsTmp;
			for (int i = 0; i < SysAppFullList.size(); i++) {
				curAppInfo = SysAppFullList.get(i);
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
			actionBar.getTabAt(USR_APPS_TAB_POS).setText(getString(R.string.user_apps)
					+" (" + UserAppFullList.size() + ")");
			actionBar.getTabAt(SYS_APPS_TAB_POS).setText(getString(R.string.sys_apps)
					+" (" + SysAppFullList.size() + ")");
//			actionBar.getTabAt(ALL_APPS_TAB_POS).setText(
//					"ALL APPS (" + AllAppList.size() + ")");

			// list设置数据
			sysAppsFrg.setData(sectionTextList, sectionItemsMap);
			
			usrAppsFrg.setListSortType(Utilities.getUserAppListSortType(thisActivityCtx));
			usrAppsFrg.setData(UserAppFullList);
			
			mUserAppListAdapter = (UserAppListAdapter)usrAppsFrg.getListAdapter();
			mSysAppListAdapter = (SysAppListAdapter)sysAppsFrg.getListAdapter();
			
			registerReceiver(mAppUpdateReceiver, AppIntentFilter);
			registerReceiver(LangUpdateReceiver , LangIntentFilter);
		}
	}

	@Override
	public void onUserAppItemLongClick(AdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
//		AppInfo selectItem = mUserAppListAdapter.getItem(position);
//		if(selectItem.selected)
//		{
//			UserAppActionModeSelectCnt--;
//			selectItem.selected = false;
//		}
//		else
//		{
//			UserAppActionModeSelectCnt++;
//			selectItem.selected = true;
//		}
//		
//		if (mActionMode == null) {	
//			mActionMode = startActionMode(mActionModeCallback);	
//		}
//		
//		if(UserAppActionModeSelectCnt < mUserAppListAdapter.getCount())
//		{
//			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.select_all);
//			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_select_all);
//		}
//		else
//		{
//			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.deselect_all);
//			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_deselect_all);
//		}
//		
//		mActionMode.setTitle(""+UserAppActionModeSelectCnt);
//		mUserAppListAdapter.notifyDataSetChanged();
		
		AppInfo selectedItem = mUserAppListAdapter.getItem(position);
		
		if (mActionMode == null) 
		{	
			mActionMode = startActionMode(mActionModeCallback);	
			selectedItem.selected = true;
			UserAppActionModeSelectCnt++;
			mActionMode.setTitle(""+UserAppActionModeSelectCnt);
			mUserAppListAdapter.notifyDataSetChanged();
		}
		else
		{
			if(mUserAppListAdapter.getCount() == 1)//只有一项
			{
				
			}
			else
			{
				//显示选择的对话框
				SelectionDialog = SelectionDialogFragment.newInstance(selectedItem,position,mUserAppListAdapter.getCount());
				SelectionDialog.show(getSupportFragmentManager(), SelectionDialogFragment.DialogTag);
			}
		}
	}



	// 用户App列表点击事件
	@Override
	public void onUserAppItemClick(View v, int position) {
		// TODO Auto-generated method stub
		AppInfo selectItem = mUserAppListAdapter.getItem(position);
		if (mActionMode != null) 
		{
			if(selectItem.selected)
			{
				UserAppActionModeSelectCnt--;
				selectItem.selected = false;
			}
			else
			{
				UserAppActionModeSelectCnt++;
				selectItem.selected = true;
			}
			
			if(UserAppActionModeSelectCnt < mUserAppListAdapter.getCount())
			{
				mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.select_all);
				mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_select_all);
			}
			else
			{
				mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.deselect_all);
				mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_deselect_all);
			}
			
			mActionMode.setTitle(""+UserAppActionModeSelectCnt);
			mUserAppListAdapter.notifyDataSetChanged();
			
		}
		else
		{
			((ActionSlideExpandableListView)usrAppsFrg.getListView()).collapse(true);
			toast.setText("user AppList " + position);
			toast.show();
		}
		 
		 
	}

	// 用户App列表中的action点击事件
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onUserAppItemActionClick(View listView, View buttonview,
			int position) {
		// TODO Auto-generated method stub
		AppInfo selectItem = mUserAppListAdapter.getItem(position);
		String targetpackageName = selectItem.packageName;
		switch (buttonview.getId()) {
		case R.id.openActionLayout:
			if(thisAppPackageName.equals(targetpackageName))//避免再次启动自己app
			{
				toast.setText("You catch me!! NAN Made app");
				toast.show();
			} 
			else
			{
				Intent intent = getPackageManager().getLaunchIntentForPackage(targetpackageName);
				if (intent != null) {
					startActivity(intent);
				} else {
					toast.setText(R.string.error);
					toast.show();
				}
			}
			break;
		case R.id.infoActionLayout:
			Utilities.showInstalledAppDetails(thisActivityCtx, targetpackageName);
			break;
		case R.id.backupActionLayout:
			String backAPKfileName = selectItem.appName+"_v"+selectItem.versionName+".apk";
			BACK_UP_FOLDER = Utilities.getBackUpAPKfileDir(thisActivityCtx);
			if(Utilities.copyFile(selectItem.apkFilePath,BACK_UP_FOLDER+backAPKfileName))
			{
				toast.setText(R.string.backup_success);
			}
			else
			{
				toast.setText(R.string.error);
			}
			toast.show();
			break;
		case R.id.uninstallActionLayout:
			Uri packageUri = Uri.parse("package:"+ targetpackageName);
			Intent uninstallIntent;
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
			} else {
				uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE,packageUri);
			}
			startActivity(uninstallIntent);
			break;
		case R.id.moreActionLayout:
//			UserAppListMoreActionDialog = new UserAppListMoreActionDialogFragment();
//			Bundle bundle = new Bundle();
//			bundle.putParcelable(UserAppListMoreActionDialogFragment.ArgumentsTag, selectItem);
//			UserAppListMoreActionDialog.setArguments(bundle);
//			UserAppListMoreActionDialog.show(getSupportFragmentManager(), UserAppListMoreActionDialogFragment.DialogTag);
			
			UserAppListMoreActionDialog = UserAppListMoreActionDialogFragment.newInstance(selectItem);
			UserAppListMoreActionDialog.show(getSupportFragmentManager(), UserAppListMoreActionDialogFragment.DialogTag);
			
			break;
		}
	}
	
	//用户app list过滤之后
	@Override
	public void onUserAppListFilterResultPublish(ArrayList<AppInfo> resultsList) {
		// TODO Auto-generated method stub
		((ActionSlideExpandableListView)usrAppsFrg.getListView()).collapse(false);
		String newTitle = getString(R.string.user_apps)+" (" + resultsList.size() + ")";
		actionBar.getTabAt(USR_APPS_TAB_POS).setText(newTitle);
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



	@Override
	public void onSysAppListFilterResultPublish(
			ArrayList<String> ResultSectionTextList,
			HashMap<String, ArrayList<AppInfo>> ResultSectionItemsMap) {
		// TODO Auto-generated method stub
		sectionTextList = ResultSectionTextList;
		sectionItemsMap = ResultSectionItemsMap;
		
		String newTitle = getString(R.string.sys_apps)+" (" + mSysAppListAdapter.getItemsCount() + ")";
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
	
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			viewPager.setPagingEnabled(false);
			menu.add(Menu.NONE, ACTIONMODE_MENU_SELECT, Menu.NONE,R.string.select_all).setIcon(R.drawable.ic_action_select_all).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add(Menu.NONE, ACTIONMODE_MENU_BACKUP, Menu.NONE,R.string.backup).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			menu.add(Menu.NONE, ACTIONMODE_MENU_SEND, Menu.NONE,R.string.send).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			menu.add(Menu.NONE, ACTIONMODE_MENU_UNINSTALL, Menu.NONE,R.string.uninstall).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			return true;
		}

		// Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
	        return false; // Return false if nothing is done
	    }

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			switch (item.getItemId())
			{
			case ACTIONMODE_MENU_SELECT:
				if(UserAppActionModeSelectCnt < mUserAppListAdapter.getCount())
				{//选择全部
					for(int i = 0;i<mUserAppListAdapter.getCount();i++)
					{
						mUserAppListAdapter.getItem(i).selected = true;
					}
					UserAppActionModeSelectCnt = mUserAppListAdapter.getCount();	
					item.setTitle(R.string.deselect_all);
					item.setIcon(R.drawable.ic_action_deselect_all);
				}
				else
				{//都不选
					for(int i = 0;i<mUserAppListAdapter.getCount();i++)
					{
						mUserAppListAdapter.getItem(i).selected = false;
					}
					UserAppActionModeSelectCnt = 0;
					item.setTitle(R.string.select_all);
					item.setIcon(R.drawable.ic_action_select_all);
				}
				mActionMode.setTitle(""+UserAppActionModeSelectCnt);
				mUserAppListAdapter.notifyDataSetChanged();
				break;
			case ACTIONMODE_MENU_BACKUP:
				if(UserAppActionModeSelectCnt > 0)
				{
					SendAfterBackUp = false;
					new BackUpApps().execute();
				}
				else
				{
					toast.setText(R.string.nothing_selected);
					toast.show();
				}
				break;
			case ACTIONMODE_MENU_SEND:
				if(UserAppActionModeSelectCnt > 0)
				{
					SendAfterBackUp = true;
					new BackUpApps().execute();
				}
				else
				{
					toast.setText(R.string.nothing_selected);
					toast.show();
				}
				break;
			case ACTIONMODE_MENU_UNINSTALL:
				if(UserAppActionModeSelectCnt > 0)
				{
					AppInfo tmpAppInfo = null;
					for(int i = 0;i<mUserAppListAdapter.getCount();i++)
					{
						tmpAppInfo = mUserAppListAdapter.getItem(i);
						if(tmpAppInfo.selected)
						{
							Uri packageUri = Uri.parse("package:"+ tmpAppInfo.packageName);
							Intent uninstallIntent;
							if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
								uninstallIntent = new Intent(Intent.ACTION_DELETE, packageUri);
							} else {
								uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE,packageUri);
							}
							startActivity(uninstallIntent);
						}
					}
					//mActionMode.finish();
				}
				else
				{
					toast.setText(R.string.nothing_selected);
					toast.show();
				}
				break;
			}
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			for(AppInfo appInfo : UserAppFullList)
			{
				appInfo.selected = false;
			}

			UserAppActionModeSelectCnt = 0;
			mUserAppListAdapter.notifyDataSetChanged();
			mActionMode = null;
			viewPager.setPagingEnabled(true);
		}
	};
	
	
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
				 toast.setText(getString(R.string.new_app_installed)+" "+NewAppName);
				 toast.show();
				 new GetApps().execute();
			 }
			 else if(intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) 
			 {
				 toast.setText(R.string.app_removed);
				 String RemovedPkgName = intent.getDataString().substring(8); 
				 ArrayList<AppInfo> tmpUserDisplayList = mUserAppListAdapter.getDisplayList();
				 for(int i = 0;i<UserAppFullList.size();i++)
				 {
					 if(UserAppFullList.get(i).packageName.equals(RemovedPkgName))
					 {
						 if(mActionMode != null)
						 {
							 if(UserAppFullList.get(i).selected)
							 {
								 UserAppActionModeSelectCnt--;
								 mActionMode.setTitle(""+UserAppActionModeSelectCnt);
							 } 
						 }
						 
						 if(UserAppListMoreActionDialog != null && 
							UserAppListMoreActionDialog.getCurrentAppInfo().packageName.equals(RemovedPkgName))
						 {
							 UserAppListMoreActionDialog.dismiss();
						 }
						 
						 toast.setText(getString(R.string.app_removed_name)+" "+UserAppFullList.get(i).appName);
						 UserAppFullList.remove(i); 
					 }
					 if(i<tmpUserDisplayList.size())
					 {
						 if(tmpUserDisplayList.get(i).packageName.equals(RemovedPkgName))
						 {
							 tmpUserDisplayList.remove(i);
						 } 
					 }
				 }

				 toast.show();
				 mUserAppListAdapter.notifyDataSetChanged();
				 ((ActionSlideExpandableListView)usrAppsFrg.getListView()).collapse(false);
				 actionBar.getTabAt(USR_APPS_TAB_POS).setText(getString(R.string.user_apps)+" (" + tmpUserDisplayList.size() + ")");
				 //new GetApps().execute();
			 }
			 else if(intent.getAction().equals(Intent.ACTION_PACKAGE_CHANGED)) 
			 {
				 new GetApps().execute();
			 }
		} 
	}

	@Override
	public void onSortTypeListItemClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
		if(usrAppsFrg.getListSortType() == which) return;
		
		switch (which)
		{
		case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
			Collections.sort(UserAppFullList, new AppNameComparator(true));
			SortMenuItem.setIcon(R.drawable.name_asc);
			break;
		case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
			Collections.sort(UserAppFullList, new AppNameComparator(false));
			SortMenuItem.setIcon(R.drawable.name_des);
			break;
		case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
			Collections.sort(UserAppFullList, new AppSizeComparator(true));
			SortMenuItem.setIcon(R.drawable.size_asc);
			break;
		case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
			Collections.sort(UserAppFullList, new AppSizeComparator(false));
			SortMenuItem.setIcon(R.drawable.size_des);
			break;
		case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
			Collections.sort(UserAppFullList, new LastModifiedComparator(true));
			SortMenuItem.setIcon(R.drawable.time_asc);
			break;
		case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
			Collections.sort(UserAppFullList, new LastModifiedComparator(false));
			SortMenuItem.setIcon(R.drawable.time_des);
			break;
		}
		
		Utilities.setUserAppListSortType(thisActivityCtx, which);
		usrAppsFrg.setListSortType(which);
		((ActionSlideExpandableListView)usrAppsFrg.getListView()).collapse(false);
		mUserAppListAdapter.notifyDataSetChanged();
	}



	@Override
	public void onUserAppMoreActionListItemClickListener(
			DialogInterface dialog, int item, AppInfo appInfo) {
		// TODO Auto-generated method stub
		if(item == 0)//google play
		{
			if (isAnyStoreInstalled) {
				startActivity(new Intent(Intent.ACTION_VIEW,
						Uri.parse("market://details?id="+ appInfo.packageName)));
			} else {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id="
								+ appInfo.packageName)));
			}
		}
		else if(item == 1) //send
		{
			String backAPKfileName = appInfo.appName+"_v"+appInfo.versionName+".apk";
			BACK_UP_FOLDER = Utilities.getBackUpAPKfileDir(thisActivityCtx);
			if(Utilities.copyFile(appInfo.apkFilePath,BACK_UP_FOLDER+backAPKfileName))
			{
	            Utilities.chooseSendByApp(thisActivityCtx, Uri.parse("file://" + BACK_UP_FOLDER+backAPKfileName));
			}
			else
			{
				toast.setText(R.string.error);
				toast.show();
			}
		}
		
	}
	
	
	private void DismissDialog(DialogFragment D_fragment)
	{
		if(D_fragment != null && D_fragment.getDialog() !=null && D_fragment.getDialog().isShowing())
		{
			D_fragment.dismiss();
		}
	}



	@Override
	public void onSelectionDialogClick(DialogInterface dialog, int selectType, int curPos) {
		// TODO Auto-generated method stub
		switch (selectType)
		{
		case SelectionDialogFragment.SELECT_ALL_ABOVE:
			for(int i = 0;i<curPos;i++)
			{
				if(!mUserAppListAdapter.getItem(i).selected)
				{
					mUserAppListAdapter.getItem(i).selected = true;
					UserAppActionModeSelectCnt++;
				}
			}
			break;
		case SelectionDialogFragment.DESELECT_ALL_ABOVE:
			for(int i = 0;i<curPos;i++)
			{
				if(mUserAppListAdapter.getItem(i).selected)
				{
					mUserAppListAdapter.getItem(i).selected = false;
					UserAppActionModeSelectCnt--;
				}
			}
			break;
		case SelectionDialogFragment.SELECT_ALL_BELOW:
			for(int i = curPos+1;i<mUserAppListAdapter.getCount();i++)
			{
				if(!mUserAppListAdapter.getItem(i).selected)
				{
					mUserAppListAdapter.getItem(i).selected = true;
					UserAppActionModeSelectCnt++;
				}
			}
			break;
		case SelectionDialogFragment.DESELECT_ALL_BELOW:
			for(int i = curPos+1;i<mUserAppListAdapter.getCount();i++)
			{
				if(mUserAppListAdapter.getItem(i).selected)
				{
					mUserAppListAdapter.getItem(i).selected = false;
					UserAppActionModeSelectCnt--;
				}
			}
			break;
		}
		
		if(UserAppActionModeSelectCnt < mUserAppListAdapter.getCount())
		{
			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.select_all);
			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_select_all);
		}
		else
		{
			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setTitle(R.string.deselect_all);
			mActionMode.getMenu().getItem(ACTIONMODE_MENU_SELECT).setIcon(R.drawable.ic_action_deselect_all);
		}
		
		mActionMode.setTitle(""+UserAppActionModeSelectCnt);
		mUserAppListAdapter.notifyDataSetChanged();
	}
}
