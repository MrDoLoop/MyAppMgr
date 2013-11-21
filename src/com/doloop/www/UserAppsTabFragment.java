package com.doloop.www;

import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.doloop.slideexpandable.library.ActionSlideExpandableListView;
import com.doloop.www.util.AppInfo;
import com.doloop.www.util.UserAppListAdapter;
import com.doloop.www.util.UserAppListAdapter.UserAppListFilterResultListener;

public class UserAppsTabFragment extends SherlockListFragment implements ListView.OnScrollListener{
	// private SysAppListAdapter mAdapter;
	private static UserAppListAdapter mAdapter;
	private static ActionSlideExpandableListView mActionSlideExpandableListView;

	private static UserAppsTabFragment uniqueInstance = null;
	public UserAppsTabFragment() 
	{
		
	}
	public synchronized  static UserAppsTabFragment getInstance() {
	       if (uniqueInstance == null) {
	           uniqueInstance = new UserAppsTabFragment();
	       }
	       return uniqueInstance;
	}
	
	
    private final class RemoveWindow implements Runnable {
        public void run() {
            removeWindow();
        }
    }

    private RemoveWindow mRemoveWindow = new RemoveWindow();
    private Handler mHandler = new Handler();
    private TextView mDialogText;
    private boolean mShowing = false;
    private boolean mListIsScrolling = false;

    private void removeWindow() {
        if (mShowing) {
            mShowing = false;
            mDialogText.setVisibility(View.INVISIBLE);
        }
    }
    
    
	public OnUserAppListItemSelectedListener mItemClickListener;

	// Container Activity must implement this interface
	public interface OnUserAppListItemSelectedListener {
		public void onUserAppItemClick(View v, int position);
	}

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
		ActionSlideExpandableListView mActionSlideExpandableListView=(ActionSlideExpandableListView)contentView.findViewById(android.R.id.list);
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
		}, R.id.openBtn, R.id.GPBtn, R.id.AppDetailsBtn, R.id.BackUpBtn, R.id.UninstallBtn);
		mActionSlideExpandableListView.setOnScrollListener(this);
		mDialogText = (TextView) contentView.findViewById(R.id.popTextView);
		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Complete the Fragment initialization ¨C particularly anything
		// that requires the parent Activity to be initialized or the
		// Fragment¡¯s view to be fully inflated.
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
		// that don¡¯t need to be updated when the Activity isn¡¯t
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
		// that aren¡¯t required when the Fragment isn¡¯t visible.
		super.onStop();
	}

	// Called when the Fragment¡¯s View has been detached.
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
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement Listener");
		}
	}

	public void setData(Context ctx, ArrayList<AppInfo> userAppList) {
		mAdapter = new UserAppListAdapter(ctx,R.layout.user_app_expandable_list_item,0,userAppList);
		mAdapter.setUserAppListFilterResultListener((UserAppListFilterResultListener)ctx);
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
			char firstLetter = mAdapter.getItem(firstVisibleItem).appName.charAt(0);
			mShowing = true;
            mDialogText.setVisibility(View.VISIBLE);
            mDialogText.setText(((Character)firstLetter).toString().toUpperCase(Locale.getDefault()));
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
}
