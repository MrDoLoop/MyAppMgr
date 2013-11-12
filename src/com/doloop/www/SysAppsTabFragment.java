package com.doloop.www;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.doloop.www.util.AppInfo;
import com.doloop.www.util.SysAppListAdapter;
import com.doloop.www.util.SysAppListAdapter.SysAppListFilterResultListener;

public class SysAppsTabFragment extends SherlockListFragment {
	private SysAppListAdapter mAdapter;

	public OnSysAppListItemSelectedListener mListener;
	
	// Container Activity must implement this interface
    public interface OnSysAppListItemSelectedListener {
        public void onSysAppItemClick(View v, int position);
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Create, or inflate the Fragment¡¯s UI, and return it.
		// If this Fragment has no UI then return null.
		View FragmentView = inflater.inflate(R.layout.sys_app_pinned_section_list, container, false);
		return FragmentView;
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Complete the Fragment initialization ¨C particularly anything
		// that requires the parent Activity to be initialized or the
		// Fragment¡¯s view to be fully inflated.
		setRetainInstance(true);
		//setEmptyText("No applications");
		
		//setListShown(false);
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
			mListener = (OnSysAppListItemSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement Listener");
		}
	}
	
	public void setData(ArrayList<String> sectionTextList, HashMap<String , ArrayList<AppInfo>> sectionItemsMap)
	{
		mAdapter = new SysAppListAdapter(getActivity(),sectionTextList,sectionItemsMap);
		mAdapter.setSysAppListFilterResultListener((SysAppListFilterResultListener)getActivity());
		setListAdapter(mAdapter);
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		mListener.onSysAppItemClick(v,position);
	}
}
