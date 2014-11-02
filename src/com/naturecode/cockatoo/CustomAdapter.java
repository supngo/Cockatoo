package com.naturecode.cockatoo;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<RowData> {
	private LayoutInflater mInflater;
	private Integer[] attributes;
	private Integer[] images;

	public CustomAdapter(Context context, List<RowData> objects, LayoutInflater inflater, Integer[] attr, Integer[] icons) {               
		super(context, attr[0], attr[1], objects);
		mInflater = inflater;
		attributes = attr;
		images = icons;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {   
		ViewHolder holder = null;
		TextView title = null;
		TextView detail = null;
		ImageView icon=null;
		RowData rowData = getItem(position);
		if(convertView == null){
			convertView = mInflater.inflate(attributes[0], null);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		}
		holder = (ViewHolder) convertView.getTag();
		title = holder.gettitle();
		title.setText(rowData.getName());
		detail = holder.getdetail();
		detail.setText(rowData.getDetail());                                                     
		icon = holder.getImage();
		icon.setImageResource(images[rowData.getImage_id()]);
		return convertView;
	}
	
	private class ViewHolder {
		private View mRow;
		private TextView title = null;
		private TextView detail = null;
		private ImageView image=null; 
		public ViewHolder(View row) {
			mRow = row;
		}
		public TextView gettitle() {
			if(null == title){
				title = (TextView) mRow.findViewById(attributes[1]);
			}
			return title;
		}     
		public TextView getdetail() {
			if(null == detail){
				detail = (TextView) mRow.findViewById(attributes[2]);
			}
			return detail;
		}
		public ImageView getImage() {
			if(null == image){
				image = (ImageView) mRow.findViewById(attributes[3]);
			}
			return image;
		}
	}
}