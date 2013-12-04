package com.doloop.www.util;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ArrayAdapterWithIcon extends ArrayAdapter<String> {
	//private String[] OptionStr;
	private int[] OptionIcon;
	
	
	public ArrayAdapterWithIcon(Context context, String[] optionStr, int[] optionIcon) {
	    super(context, android.R.layout.simple_list_item_1, optionStr);
	    //OptionStr = optionStr;
	    OptionIcon = optionIcon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = super.getView(position, convertView, parent);
	    TextView textView = (TextView) view.findViewById(android.R.id.text1);
	    textView.setCompoundDrawablesWithIntrinsicBounds(OptionIcon[position], 0, 0, 0);
	    textView.setCompoundDrawablePadding(
	            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getContext().getResources().getDisplayMetrics()));
	    return view;
	}
}
