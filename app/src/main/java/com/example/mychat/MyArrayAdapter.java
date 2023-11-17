package com.example.mychat;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyArrayAdapter extends ArrayAdapter<User> implements Filterable {
    Context context;
    List<User> user=new ArrayList<>();
    List<User> userOld=new ArrayList<>();
    List<User> userSearch;


    public MyArrayAdapter(Context context, int layoutToBeInflated, List<User> user) {
        super(context, R.layout.array_adapter, user);
        this.context = context;
        this.user = user;
        this.userOld=new ArrayList<>(user);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View row=inflater.inflate(R.layout.array_adapter,null);
        TextView txtName=(TextView)row.findViewById(R.id.txtName);
        TextView txtString=(TextView)row.findViewById(R.id.txtString);
        ImageView imgView =(ImageView)row.findViewById(R.id.imgView);
        txtName.setText(user.get(position).getName());
        txtString.setText(user.get(position).getEmail());
        imgView.setImageResource(user.get(position).getImg());
        return(row);
    }

    @Override
    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String search=constraint.toString();
                if(search.isEmpty())
                {
                    userSearch=new ArrayList<>(userOld);
                }
                else
                {
                    List<User> searchName=new ArrayList<>();
                    for(User i:userOld)
                    {
                        if(i.getName().toLowerCase().contains(search.toLowerCase())){
                            searchName.add(i);
                        }
                    }
                    userSearch=searchName;
                }

                FilterResults filterResults=new FilterResults();
                filterResults.values = userSearch;
                filterResults.count = userSearch.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                user.clear();
                user.addAll((List<User>) results.values);
                notifyDataSetChanged();
            }
        };
    }
}
