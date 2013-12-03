package com.doloop.www;

import com.doloop.www.util.AppInfo;
import com.doloop.www.util.Utilities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class UserAppListMoreActionDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "UserAppListMoreActionDialogFragment";
	
	private AppInfo mAppinfo;
	
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
		return this.mAppinfo;
	}
	
	public UserAppListMoreActionDialogFragment()
	{
		
	}
	
	public UserAppListMoreActionDialogFragment(AppInfo appInfo)
	{
		this.mAppinfo = appInfo;
	}
	
	 @Override
	 public void onAttach(Activity activity) {
		 super.onAttach(activity);
	     try {
	    	 mUserAppMoreActionListItemClickListener = (UserAppMoreActionListItemClickListener) activity;
	     } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement SortTypeListItemClickListener");
	     }
	 }
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Build the dialog and set up the button click handlers
	    	moreActionOpt = new String[] {getActivity().getString(R.string.google_play), getActivity().getString(R.string.send)};
	    	ArrayAdapterWithIcon adapter = new ArrayAdapterWithIcon(getActivity(), moreActionOpt, moreActionOptIcon);
	    	//mAppinfo = getArguments().getParcelable(ArgumentsTag); 
	    	
	    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(mAppinfo.appName).setIcon(Utilities.ZoomDrawable(mAppinfo.appIcon,getActivity()))
            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item ) {
                	mUserAppMoreActionListItemClickListener.onUserAppMoreActionListItemClickListener(dialog, item, mAppinfo);
                	dialog.dismiss();
                }
            });
	        AlertDialog dialog = builder.create();
	        dialog.setCanceledOnTouchOutside(true);
	        return dialog;
	    }


	    
	    
	    private class ArrayAdapterWithIcon extends ArrayAdapter<String> 
	    {
	    	public ArrayAdapterWithIcon(Context context, String[] items, int[] moreactionopticon) {
	    	    super(context, android.R.layout.select_dialog_item, items);
	    	}

	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    	    View view = super.getView(position, convertView, parent);
	    	    TextView textView = (TextView) view.findViewById(android.R.id.text1);
	    	    textView.setCompoundDrawablesWithIntrinsicBounds(moreActionOptIcon[position], 0, 0, 0);
	    	    textView.setCompoundDrawablePadding(
	    	            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getContext().getResources().getDisplayMetrics()));
	    	    return view;
	    	}
	    }
}
