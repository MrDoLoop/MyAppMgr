package com.doloop.www;

import com.doloop.www.util.AppInfo;
import com.doloop.www.util.ArrayAdapterWithIcon;
import com.doloop.www.util.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

//@SuppressLint("ValidFragment")
public class UserAppListMoreActionDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "UserAppListMoreActionDialogFragment";
	
	private static AppInfo mAppinfo;
	
	private String[] moreActionOpt;// = {"Google Play","Send"};
	private final static int[] moreActionOptIcon = {R.drawable.google_paly_80x80,R.drawable.send1_80x80};

	public final static String ArgumentsTag = "ArgumentsTag";
	
	public UserAppMoreActionListItemClickListener mUserAppMoreActionListItemClickListener;

	// Container Activity must implement this interface
	public interface UserAppMoreActionListItemClickListener {
		public void onUserAppMoreActionListItemClickListener(DialogInterface dialog, int item, AppInfo appInfo);
	}

	public AppInfo getCurrentAppInfo()
	{
		return mAppinfo;
	}
	
	public static UserAppListMoreActionDialogFragment newInstance(AppInfo appInfo) {
		UserAppListMoreActionDialogFragment fragmentInstance = new UserAppListMoreActionDialogFragment();
		mAppinfo = appInfo;
		return fragmentInstance;
	}
	
	 @Override
	 public void onAttach(Activity activity) {
		 super.onAttach(activity);
	     try {
	    	 mUserAppMoreActionListItemClickListener = (UserAppMoreActionListItemClickListener) activity;
	     } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement UserAppMoreActionListItemClickListener");
	     }
	 }
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Build the dialog and set up the button click handlers
	    	moreActionOpt = new String[] {getActivity().getString(R.string.google_play), getActivity().getString(R.string.send)};
	    	ArrayAdapterWithIcon adapter = new ArrayAdapterWithIcon(getActivity(), moreActionOpt, moreActionOptIcon);
	    	//mAppinfo = getArguments().getParcelable(ArgumentsTag); 
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    	if(mAppinfo != null)
	    	{
	    	    builder.setTitle(mAppinfo.appName).setIcon(Utilities.ZoomDrawable(mAppinfo.appIcon,getActivity()))
	            .setAdapter(adapter, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int item ) {
	                	mUserAppMoreActionListItemClickListener.onUserAppMoreActionListItemClickListener(dialog, item, mAppinfo);
	                	dialog.dismiss();
	                }
	            });
	    	}
	    
	        AlertDialog dialog = builder.create();
	        dialog.setCanceledOnTouchOutside(true);
	        return dialog;
	    }
}
