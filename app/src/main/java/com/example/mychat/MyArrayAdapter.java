package com.example.mychat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyArrayAdapter extends ArrayAdapter {
    Context context;
    String[] id; //get id, không hiện lên, để ánh xạ các thuộc tính còn lại
    String[] name; //tên người liên hệ
    String[] string;
    //chuỗi nếu như trong ContactActivity sẽ hiện username,
    // nếu như trong ChatActivity sẽ hiện tin nhắn gần nhất
    Integer[] img; //hình ảnh, ảnh đại diện
    public MyArrayAdapter(Context context, int layoutToBeInflated, String[] name, String[] string,Integer[] img) {
        super(context,R.layout.array_adapter,name);
        this.context=context;
        this.name=name;
        this.string=string;
        this.img=img;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View row=inflater.inflate(R.layout.array_adapter,null);
        TextView txtName=(TextView)row.findViewById(R.id.txtName);
        TextView txtString=(TextView)row.findViewById(R.id.txtString);
        ImageView imgView =(ImageView)row.findViewById(R.id.imgView);
        txtName.setText(name[position]);
        txtString.setText(string[position]);
        imgView.setImageResource(img[position]);
        return(row);
    }
}
