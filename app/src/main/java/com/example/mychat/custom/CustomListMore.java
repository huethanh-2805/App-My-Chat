package com.example.mychat.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychat.R;

public class CustomListMore extends ArrayAdapter {
    Context context;
    String[] items;
    Integer[] icons;
    public CustomListMore(Context context, int layoutToBeInflated,String[] items,Integer[] icons) {
        super(context, R.layout.custom_listview_more,items);
        this.context=context;
        this.icons=icons;
        this.items=items;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View row=inflater.inflate(R.layout.custom_listview_more,null);
        TextView name=(TextView)row.findViewById(R.id.txtName);
        ImageView icon=(ImageView)row.findViewById(R.id.iconItem);
        name.setText(items[position]);
        icon.setImageResource(icons[position]);
        return(row);
    }
}
