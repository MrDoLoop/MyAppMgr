package com.doloop.www.util;

import java.util.ArrayList;

import com.doloop.www.R;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class IndexBarView extends LinearLayout {
	
	public void setPopView(View view)
	{
		mPopView = view;
	}
	
	private OnIndexItemClickListener mOnIndexItemClickListener;
	
	public interface OnIndexItemClickListener {
		void onItemClick(String s);
	}
	
	public void setOnIndexItemClickListener(OnIndexItemClickListener listener) {
		this.mOnIndexItemClickListener = listener;
	}
	
	public final static String[] mIndexArray = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
			"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T","U", "V", "W", "X", "Y", "Z", "#" };
	
	private ArrayList<TextView> mIndexTextViewList = new ArrayList<TextView>();
	private PopupWindow mPopupWindow;
	private View mPopView = null;
	private TextView mPopupText;
	private Handler handler = new Handler();
	int choose = -1;
	private int singleIndexHeight = 0;
	
	
	public IndexBarView(Context context) {
		super(context);
		InitIndexBar();
		// TODO Auto-generated constructor stub
	}

	public IndexBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		InitIndexBar();
	}
	
	public void InitIndexBar()
	{
		removeAllViews(); 
		mIndexTextViewList.clear();
		setOrientation(LinearLayout.VERTICAL);
		setPadding(0, 10, 0, 10);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
	                LayoutParams.WRAP_CONTENT, 1);
		TextView tmpTV = null;
		for(int i = 0;i<mIndexArray.length;i++)
		{
			tmpTV = new TextView(getContext());
			tmpTV.setGravity(Gravity.CENTER);
			tmpTV.setLayoutParams(params);
			tmpTV.setTextColor(Color.GRAY);
			tmpTV.setText(mIndexArray[i]);
			tmpTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
			addView(tmpTV);
			mIndexTextViewList.add(tmpTV);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float TouchEventYpos = event.getY();
		final int oldChoose = choose;
		singleIndexHeight = getHeight()/mIndexArray.length;	
		int c = (int)(TouchEventYpos/singleIndexHeight);
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			
			//setBackgroundColor(Color.LTGRAY);
			setBackgroundResource(R.drawable.rounded_rectangle_shape);
			if (oldChoose != c) {
				if (c >= 0 && c < mIndexArray.length) {
					clearIndexListItemBG(Color.BLACK,true);
					//mIndexTextViewList.get(c).setBackgroundColor(Color.RED);
					mIndexTextViewList.get(c).setBackgroundResource(R.drawable.rounded_rectangle_shape_index_selected);
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}
			Log.i("ttt", "DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			if (oldChoose != c) {
				if (c >= 0 && c < mIndexArray.length) {
					clearIndexListItemBG(Color.BLACK,true);
					//mIndexTextViewList.get(c).setBackgroundColor(Color.RED);
					mIndexTextViewList.get(c).setBackgroundResource(R.drawable.rounded_rectangle_shape_index_selected);
					performItemClicked(c);
					choose = c;
					invalidate();
				}
			}
			Log.i("ttt", "MOVE");
			break;
		case MotionEvent.ACTION_UP:
//			setBackgroundColor(Color.TRANSPARENT);
//			clearIndexListItemBG(Color.GRAY,false);
//			choose = -1;
//			dismissPopup();
			reset();
			Log.i("ttt", "UP");
			break;
		}
		return true;
	}
	
	private void clearIndexListItemBG(int TextColor, boolean bold)
	{
		for(TextView tv: mIndexTextViewList)
		{
			tv.setBackgroundColor(Color.TRANSPARENT);
			tv.setTextColor(TextColor);
			
			if(bold)
				tv.setTypeface(null,Typeface.BOLD);
			else
				tv.setTypeface(null,Typeface.NORMAL);
		}
	}
	
	private void showPopup(int item) {
		if(mPopView != null)
		{
			mPopView.setVisibility(View.VISIBLE);
			((TextView)mPopView).setText(mIndexArray[item]);
			return;
		}
		
		
		if (mPopupWindow == null) {
			View contentView = LayoutInflater.from(getContext())  
	                .inflate(R.layout.popup_text, null); 
			mPopupText = (TextView) contentView.findViewById(R.id.popText);
			mPopupWindow = new PopupWindow(getContext());  
			mPopupWindow.setContentView(contentView); 
			mPopupWindow.setWidth(LayoutParams.WRAP_CONTENT); 
			mPopupWindow.setHeight(LayoutParams.WRAP_CONTENT); 
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent)); 
			
			handler.removeCallbacks(dismissRunnable);
			//mPopupText = new TextView(getContext());
			//mPopupText.setBackgroundColor(Color.GRAY);
			//mPopupText.setBackgroundResource(R.drawable.rounded_rectangle_shape);
			//mPopupText.setTextColor(Color.CYAN);
			//mPopupText.setTextSize(25);//mScaledDensity
			//mPopupText.setGravity(Gravity.CENTER_HORIZONTAL
			//					| Gravity.CENTER_VERTICAL);
			//mPopupWindow = new PopupWindow(mPopupText, 150, 150);
			
			
		}

		
		mPopupText.setText(mIndexArray[item]);
		if (mPopupWindow.isShowing()) {
			mPopupWindow.update();
		} else {
			mPopupWindow.showAtLocation(getRootView(),
					Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		}
	}

	private void dismissPopup() {
		handler.postDelayed(dismissRunnable, 800);
	}

	Runnable dismissRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if(mPopView != null)
			{
				mPopView.setVisibility(View.INVISIBLE);
				return;
			}
			if (mPopupWindow != null) {
				mPopupWindow.dismiss();
			}
		}
	};
	
	private void performItemClicked(int item) {
		showPopup(item);
		if (mOnIndexItemClickListener != null) {
			mOnIndexItemClickListener.onItemClick(mIndexArray[item]);
			//showPopup(item);
		}
	}
	
	
	public void reset()
	{
		setBackgroundColor(Color.TRANSPARENT);
		clearIndexListItemBG(Color.GRAY,false);
		choose = -1;
		dismissPopup();
	}
	
}
