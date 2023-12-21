package com.example.mychat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.mychat.R;
import com.example.mychat.object.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends ArrayAdapter<User> implements Filterable {
    Context context;
    List<User> user=new ArrayList<>();
    List<User> userOld=new ArrayList<>();
    List<User> userSearch;


    public ChatAdapter(Context context, int layoutToBeInflated, List<User> user) {
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
        CircleImageView imgView =(CircleImageView) row.findViewById(R.id.imgView);
        txtName.setText(user.get(position).getName());
        txtString.setText(user.get(position).getEmail());
//        imgView.setImageResource(user.get(position).getImg());
        if (user.get(position).getImg()!=null){
            Picasso.get().load(user.get(position).getImg()).into(imgView);
        }
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
