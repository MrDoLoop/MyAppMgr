package com.doloop.www;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class SelectionDialogFragment extends DialogFragment {
	
	public final static String DialogTag = "SelectiongDialogFragment";
	
	public final static String ArgumentsTag  = "ArgumentsTag";
	
	private static String[] selectionOpt;// = {"Select all above","Select all below"};
	
	public final static int SELECT_ALL_ABOVE = 0;
	public final static int DESELECT_ALL_ABOVE = 1;
	public final static int SELECT_ALL_BELOW = 2;
	public final static int DESELECT_ALL_BELOW = 3;

	public SelectionDialogClickListener mSelectionDialogClickListener;

	// Container Activity must implement this interface
	public interface SelectionDialogClickListener {
		public void onSelectionDialogClick(DialogInterface dialog, int selectType, int curPos);
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
	    	int[] arguments = getArguments().getIntArray(ArgumentsTag); //0当前位置, 1,列表长度
	    	final int curPos = arguments[0];
	    	final int listTotleSize = arguments[1];
	    	if(curPos == 0)//列表第一项
	    	{
	    		selectionOpt = new String[2];
	    		selectionOpt[0] = getActivity().getString(R.string.select_all_below);
	    		selectionOpt[1] = getActivity().getString(R.string.deselect_all_below);
	    	}
	    	else if(curPos == listTotleSize-1)//列表最后一项
	    	{
	    		selectionOpt = new String[2];
	    		selectionOpt[0] = getActivity().getString(R.string.select_all_above);
	    		selectionOpt[1] = getActivity().getString(R.string.deselect_all_above);
	    	}
	    	else 
	    	{
	    		selectionOpt = new String[4];
	    		selectionOpt[0] = getActivity().getString(R.string.select_all_above);
	    		selectionOpt[1] = getActivity().getString(R.string.deselect_all_above);
	    		selectionOpt[2] = getActivity().getString(R.string.select_all_below);
	    		selectionOpt[3] = getActivity().getString(R.string.deselect_all_below);
	    	}

	        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	        builder.setItems(selectionOpt, new DialogInterface.OnClickListener(){
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
							
						}
						else if(curPos == listTotleSize-1)//列表最后一项
						{
							
						}
					}
					dialog.dismiss();
				}

			});
	        AlertDialog dialog = builder.create();
	        dialog.setCanceledOnTouchOutside(true);
	        return dialog;
	    }
}
