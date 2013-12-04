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
public class SelectionDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "SelectiongDialogFragment";
	
	public final static String ArgumentsTag  = "ArgumentsTag";
	
	private String[] selectionOpt;// = {"Select all above","Select all below"};
	private int[] selectionOptIcon;// = {"Select all above","Select all below"};
	
	public final static int SELECT_ALL_ABOVE = 0;
	public final static int DESELECT_ALL_ABOVE = 1;
	public final static int SELECT_ALL_BELOW = 2;
	public final static int DESELECT_ALL_BELOW = 3;

	public SelectionDialogClickListener mSelectionDialogClickListener;

	// Container Activity must implement this interface
	public interface SelectionDialogClickListener {
		public void onSelectionDialogClick(DialogInterface dialog, int selectType, int curPos);
	}
	
	private static AppInfo mAppinfo;
	
	public SelectionDialogFragment()
	{
		
	}
	
	public SelectionDialogFragment(AppInfo appInfo)
	{
		mAppinfo = appInfo;
	}
	
	
	 @Override
	 public void onAttach(Activity activity) {
		 super.onAttach(activity);
	     try {
	    	 mSelectionDialogClickListener = (SelectionDialogClickListener) activity;
	     } catch (ClassCastException e) {
	            throw new ClassCastException(activity.toString()
	                    + " must implement SelectionDialogClickListener");
	     }
	 }
	    @Override
	    public Dialog onCreateDialog(Bundle savedInstanceState) {
	        // Build the dialog and set up the button click handlers
	    	String[] arguments = getArguments().getStringArray(ArgumentsTag); //0当前位置, 1,列表长度, 2: appName
	    	final int curPos = Integer.valueOf(arguments[0]);
	    	final int listTotleSize = Integer.valueOf(arguments[1]);
	    	String dialogTitle = arguments[2];
	    	if(curPos == 0)//列表第一项
	    	{
	    		selectionOpt = new String[2];
	    		selectionOpt[0] = getActivity().getString(R.string.select_all_below);
	    		selectionOpt[1] = getActivity().getString(R.string.deselect_all_below);
	    		
	    		selectionOptIcon = new int[2];
	    		selectionOptIcon[0] = R.drawable.select_all_below;
	    		selectionOptIcon[1] = R.drawable.deselect_all_below;
	    	}
	    	else if(curPos == listTotleSize-1)//列表最后一项
	    	{
	    		selectionOpt = new String[2];
	    		selectionOpt[0] = getActivity().getString(R.string.select_all_above);
	    		selectionOpt[1] = getActivity().getString(R.string.deselect_all_above);
	    		
	    		selectionOptIcon = new int[2];
	    		selectionOptIcon[0] = R.drawable.select_all_above;
	    		selectionOptIcon[1] = R.drawable.deselect_all_above;
	    	}
	    	else 
	    	{
	    		selectionOpt = new String[4];
	    		selectionOpt[0] = getActivity().getString(R.string.select_all_above);
	    		selectionOpt[1] = getActivity().getString(R.string.deselect_all_above);
	    		selectionOpt[2] = getActivity().getString(R.string.select_all_below);
	    		selectionOpt[3] = getActivity().getString(R.string.deselect_all_below);
	    		
	    		selectionOptIcon = new int[4];
	    		selectionOptIcon[0] = R.drawable.select_all_above;
	    		selectionOptIcon[1] = R.drawable.deselect_all_above;
	    		selectionOptIcon[2] = R.drawable.select_all_below;
	    		selectionOptIcon[3] = R.drawable.deselect_all_below;
	    	}

	    	ArrayAdapterWithIcon adapter = new ArrayAdapterWithIcon(getActivity(), selectionOpt, selectionOptIcon);

	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setTitle(dialogTitle).setIcon(Utilities.ZoomDrawable(mAppinfo.appIcon,getActivity()))
	        .setAdapter(adapter, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					if(selectionOpt.length == 4)
					{
						mSelectionDialogClickListener.onSelectionDialogClick(dialog, which, curPos);
					}
					else if(selectionOpt.length == 2)
					{
						if(curPos == 0)//列表第一项
						{
							if(which == 0)
							{
								mSelectionDialogClickListener.onSelectionDialogClick(dialog, SELECT_ALL_BELOW, curPos);
							}
							else if(which == 1)
							{
								mSelectionDialogClickListener.onSelectionDialogClick(dialog, DESELECT_ALL_BELOW, curPos);
							}
						}
						else if(curPos == listTotleSize-1)//列表最后一项
						{
							if(which == 0)
							{
								mSelectionDialogClickListener.onSelectionDialogClick(dialog, SELECT_ALL_ABOVE, curPos);
							}
							else if(which == 1)
							{
								mSelectionDialogClickListener.onSelectionDialogClick(dialog, DESELECT_ALL_ABOVE, curPos);
							}
						}
					}
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
	    	    super(context, android.R.layout.simple_list_item_1, items);
	    	}

	    	@Override
	    	public View getView(int position, View convertView, ViewGroup parent) {
	    	    View view = super.getView(position, convertView, parent);
	    	    TextView textView = (TextView) view.findViewById(android.R.id.text1);
	    	    textView.setCompoundDrawablesWithIntrinsicBounds(selectionOptIcon[position], 0, 0, 0);
	    	    textView.setCompoundDrawablePadding(
	    	            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getContext().getResources().getDisplayMetrics()));
	    	    return view;
	    	}
	    }
	    
	    
	    
}
