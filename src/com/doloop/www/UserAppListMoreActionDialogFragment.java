package com.doloop.www;

import com.doloop.www.util.AppInfo;

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

public class UserAppListMoreActionDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "UserAppListMoreActionDialogFragment";
	
	private AppInfo mAppinfo;
	
	private static final String[] moreActionOpt = {"Google Play","Send"};
	private static final Integer[] moreActionOptIcon = {R.drawable.google_paly_80x80,R.drawable.send1_80x80};

	public UserAppMoreActionListItemClickListener mUserAppMoreActionListItemClickListener;

	// Container Activity must implement this interface
	public interface UserAppMoreActionListItemClickListener {
		public void onUserAppMoreActionListItemClickListener(DialogInterface dialog, int item, AppInfo appInfo);
	}
	
	public UserAppListMoreActionDialogFragment()
	{
		
	}
	
	public UserAppListMoreActionDialogFragment(AppInfo appInfo)
	{
		this.mAppinfo = appInfo;
	}
	
	public AppInfo getCurrentAppInfo()
	{
		return this.mAppinfo;
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
	    	ArrayAdapterWithIcon adapter = new ArrayAdapterWithIcon(getActivity(), moreActionOpt, moreActionOptIcon);

	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle("More actions")
            .setAdapter(adapter, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item ) {
                	mUserAppMoreActionListItemClickListener.onUserAppMoreActionListItemClickListener(dialog, item, mAppinfo);
                	dialog.dismiss();
                }
            });
	        return builder.create();
	    }
	    
	    
	    private class ArrayAdapterWithIcon extends ArrayAdapter<String> 
	    {

	    	//private ArrayList<Integer> images;

//	    	public ArrayAdapterWithIcon(Context context, List<String> items, List<Integer> images) {
//	    	    super(context, android.R.layout.select_dialog_item, items);
//	    	    this.images = images;
//	    	}

	    	public ArrayAdapterWithIcon(Context context, String[] items, Integer[] moreactionopticon) {
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
