package com.example.mychat;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private ArrayList<String> list;

    public GridViewAdapter(Context context, ArrayList<String> uriList) {
        super(context,R.layout.grid_item_layout,uriList);
        context = context;
        list = uriList;
    }
    @Override
    public View getView(int position, @NonNull View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.grid_item_layout, null);
        ImageView imageView = convertView.findViewById(R.id.gridItem);
        Glide.with(context).load(list.get(position)).into(imageView);
        return view;
    }
}
