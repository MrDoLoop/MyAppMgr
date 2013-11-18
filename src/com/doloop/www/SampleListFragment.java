package com.doloop.www;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SampleListFragment extends ListFragment {

	private OnMenuListItemClickListener mListener;
	
	// Container Activity must implement this interface
    public interface OnMenuListItemClickListener {
        public void OnMenuListItemClick(ListView MenulistView, View v, int position, long id);
    }
	
    public void setOnMenuListItemClickListener(OnMenuListItemClickListener ItemClickListener)
    {
    	this.mListener = ItemClickListener;
    }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.slide_menu_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		SampleAdapter adapter = new SampleAdapter(getActivity());
		for (int i = 0; i < 20; i++) {
			adapter.add(new SampleItem("Sample List " + (i+1), android.R.drawable.ic_menu_search));
		}
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		if(mListener != null)
			mListener.OnMenuListItemClick(l, v, position, id);
	}




	public class SampleItem {
		public String tag;
		public int iconRes;
		public SampleItem(String tag, int iconRes) {
			this.tag = tag; 
			this.iconRes = iconRes;
		}
	}

	public class SampleAdapter extends ArrayAdapter<SampleItem> {

		public SampleAdapter(Context context) {
			super(context, 0);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			SampleItem tmpItem = getItem(position);
			
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.row_icon);
				holder.text = (TextView) convertView.findViewById(R.id.row_title);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.icon .setImageResource(tmpItem.iconRes);
			holder.text.setText(tmpItem.tag);

			return convertView;
		}

	}
	
	
	static class ViewHolder
  	{
		ImageView icon;
		TextView text;
  	}
	
}
