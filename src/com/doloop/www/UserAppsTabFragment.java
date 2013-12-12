package com.doloop.www;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.doloop.slideexpandable.library.ActionSlideExpandableListView;
import com.doloop.www.util.AppInfo;
import com.doloop.www.util.UserAppListAdapter;
import com.doloop.www.util.UserAppListAdapter.UserAppListFilterResultListener;

public class UserAppsTabFragment extends SherlockListFragment implements 
	ListView.OnScrollListener, AdapterView.OnItemLongClickListener {

	
	private static Context mContext;
	private static int currentSortType = SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC;
	
	public void setListSortType(int sortType)
	{
		currentSortType = sortType;
		switch (currentSortType)
		{
		case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
		case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
            //mDialogText = nameText;
			int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, mContext.getResources().getDisplayMetrics());
			//放在list的中间
			LayoutParams paramsFixSize = new LayoutParams(size,size); 
			paramsFixSize.alignWithParent = true;
			paramsFixSize.addRule(RelativeLayout.CENTER_IN_PARENT);
			
			mDialogText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
			mDialogText.setPadding(0, 0, 0, 0);
			mDialogText.setLayoutParams(paramsFixSize);
			break;
		case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
		case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
		case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
		case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, mContext.getResources().getDisplayMetrics());
			LayoutParams paramsWrapContent = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);  
			paramsWrapContent.alignWithParent = true;
			//paramsWrapContent.addRule(RelativeLayout.CENTER_IN_PARENT);
			//放在list的top
			paramsWrapContent.addRule(RelativeLayout.CENTER_HORIZONTAL);
			paramsWrapContent.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			paramsWrapContent.setMargins(0, padding, 0, 0);
			
			mDialogText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
			mDialogText.setPadding(padding, 0, padding, 0);
			mDialogText.setLayoutParams(paramsWrapContent);
			break;
		}
	}
	
	public int getListSortType()
	{
		return currentSortType;
	}
	
	private static UserAppListAdapter mAdapter;
	private static ActionSlideExpandableListView mActionSlideExpandableListView;

	private static UserAppsTabFragment uniqueInstance = null;
	public UserAppsTabFragment() 
	{
		
	}
	public synchronized  static UserAppsTabFragment getInstance(Context ctx) {
		if (uniqueInstance == null) {
			uniqueInstance = new UserAppsTabFragment();
		}
		mContext = ctx;
		return uniqueInstance;
	}
	
    private final class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }

    private RemoveWindow mRemoveWindow = new RemoveWindow();
    private Handler mHandler = new Handler();
    private static TextView mDialogText;
    private boolean mShowing = false;
    private boolean mListIsScrolling = false;

    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
        }
    }
    
    //list长按事件
	public OnUserAppListItemLongClickListener mItemLongClickListener;

	// Container Activity must implement this interface
	public interface OnUserAppListItemLongClickListener {
		public void onUserAppItemLongClick(AdapterView<?> parent, View view, int position, long id);
	}
    
    //list条目点击事件
	public OnUserAppListItemSelectedListener mItemClickListener;

	// Container Activity must implement this interface
	public interface OnUserAppListItemSelectedListener {
		public void onUserAppItemClick(View v, int position);
	}
	//list action 点击事件
	public OnUserAppListItemActionClickListener mActionClickListener;

	// Container Activity must implement this interface
	public interface OnUserAppListItemActionClickListener {
		public void onUserAppItemActionClick(View listView, View buttonview, int position);
	}

	@Override
	public ListView getListView() {
		// TODO Auto-generated method stub
		return mActionSlideExpandableListView;
	}
	
	@Override
	public ListAdapter getListAdapter() {
		// TODO Auto-generated method stub
		return mAdapter;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View contentView = inflater.inflate(R.layout.user_app_slide_expandable_list,
				container, false);
		mActionSlideExpandableListView = (ActionSlideExpandableListView) contentView.findViewById(android.R.id.list);
		mActionSlideExpandableListView.setItemActionListener(
				new ActionSlideExpandableListView.OnActionClickListener() {
				@Override
				public void onClick(View listView, View buttonview, int position) {
					mActionClickListener.onUserAppItemActionClick(listView, buttonview, position);
				}

			// note that we also add 1 or more ids to the
			// setItemActionListener
			// this is needed in order for the listview to discover the
			// buttons
		}, R.id.openActionLayout, R.id.infoActionLayout, R.id.backupActionLayout, R.id.uninstallActionLayout, R.id.moreActionLayout);
		mActionSlideExpandableListView.setOnScrollListener(this);
		mActionSlideExpandableListView.setOnItemLongClickListener(this);
		mDialogText = (TextView) contentView.findViewById(R.id.popTextView);
		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Complete the Fragment initialization – particularly anything
		// that requires the parent Activity to be initialized or the
		// Fragment’s view to be fully inflated.
		setRetainInstance(true);
		// setEmptyText("No applications");

		// setListShown(false);
	}

	// Called at the start of the visible lifetime.
	@Override
	public void onStart() {
		super.onStart();
		// Apply any required UI change now that the Fragment is visible.
	}

	// Called at the start of the active lifetime.
	@Override
	public void onResume() {
		super.onResume();
		// Resume any paused UI updates, threads, or processes required
	}

	// Called at the end of the active lifetime.
	@Override
	public void onPause() {
		// Suspend UI updates, threads, or CPU intensive processes
		// that don’t need to be updated when the Activity isn’t
		// the active foreground activity.
		// Persist all edits or state changes
		// as after this call the process is likely to be killed.
		super.onPause();
	}

	// Called to save UI state changes at the
	// end of the active lifecycle.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate, onCreateView, and
		// onCreateView if the parent Activity is killed and restarted.
		super.onSaveInstanceState(savedInstanceState);
	}

	// Called at the end of the visible lifetime.
	@Override
	public void onStop() {
		// Suspend remaining UI updates, threads, or processing
		// that aren’t required when the Fragment isn’t visible.
		super.onStop();
	}

	// Called when the Fragment’s View has been detached.
	@Override
	public void onDestroyView() {
		// Clean up resources related to the View.
		super.onDestroyView();
	}

	// Called at the end of the full lifetime.
	@Override
	public void onDestroy() {
		// Clean up any resources including ending threads,
		// closing database connections etc.
		super.onDestroy();
	}

	// Called when the Fragment has been detached from its parent Activity.
	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Get a reference to the parent Activity.
		try {
			mItemClickListener = (OnUserAppListItemSelectedListener) activity;
			mActionClickListener = (OnUserAppListItemActionClickListener) activity;
			mItemLongClickListener = (OnUserAppListItemLongClickListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement Listener");
		}
	}

	public void setData(ArrayList<AppInfo> userAppList) {
		mAdapter = new UserAppListAdapter(mContext,R.layout.user_app_expandable_list_item,0,userAppList);
		mAdapter.setUserAppListFilterResultListener((UserAppListFilterResultListener)mContext);
		mActionSlideExpandableListView.setAdapter(mAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		mItemClickListener.onUserAppItemClick(v, position);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if(mAdapter == null) return;
		
		Log.i("ttt", "user appList onScroll");
		Log.i("ttt", "mListIsScrolling is "+mListIsScrolling);
		
		if(mListIsScrolling) 
		{
			AppInfo firstVisiableApp = mAdapter.getItem(firstVisibleItem);
			if(firstVisiableApp == null) return;
			
			switch (currentSortType)
			{
			case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_ASC:
			case SortTypeDialogFragment.LIST_SORT_TYPE_NAME_DES:
				char firstLetter = firstVisiableApp.appName.charAt(0);
				mDialogText.setText(((Character)firstLetter).toString().toUpperCase(Locale.getDefault()));
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_ASC:
			case SortTypeDialogFragment.LIST_SORT_TYPE_SIZE_DES:
				mDialogText.setText(firstVisiableApp.appSizeStr);
				break;
			case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_ASC:
			case SortTypeDialogFragment.LIST_SORT_TYPE_LAST_MOD_TIME_DES:
				mDialogText.setText(firstVisiableApp.lastModifiedTimeStr);
				break;
			}
			
			mShowing = true;
            mDialogText.setVisibility(View.VISIBLE);
            mHandler.removeCallbacks(mRemoveWindow);
            mHandler.postDelayed(mRemoveWindow, 1000);
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		switch (scrollState)
		{
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				mListIsScrolling = false;
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				mListIsScrolling = true;
				break;
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				mListIsScrolling = true;
				break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		mItemLongClickListener.onUserAppItemLongClick(parent, view, position, id);
		return true;
	}
	
	public static void ExpandAnimationFinsh(int ExpandableViewBtm)
	{
		int[] loc = new int[2];
		mActionSlideExpandableListView.getLocationOnScreen(loc);
		int moveHeight = ExpandableViewBtm - (loc[1]+mActionSlideExpandableListView.getMeasuredHeight());
		if(moveHeight > 0)
		{
			mActionSlideExpandableListView.smoothScrollBy(moveHeight, 800);
		}
	}
}
