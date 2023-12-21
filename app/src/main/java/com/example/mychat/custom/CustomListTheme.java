package com.example.mychat.custom;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.mychat.R;

public class CustomListTheme extends ArrayAdapter {
    Context context;
    String[] theme;
    Integer[] colors;
    public CustomListTheme(Context context, int layoutToBeInflated,String[] theme,Integer[] colors) {
        super(context, R.layout.custom_list_themes,theme);
        this.context=context;
        this.theme=theme;
        this.colors=colors;
    }

//    public CustomListTheme(@NonNull Context context, int resource) {
//        super(context, resource);
//    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View row=inflater.inflate(R.layout.custom_list_themes,null);
        TextView name=(TextView)row.findViewById(R.id.txtnameTheme);
        ImageView icon=(ImageView)row.findViewById(R.id.imgTheme);
        name.setText(theme[position]);
        icon.setImageResource(colors[position]);
        return(row);
    }
}
