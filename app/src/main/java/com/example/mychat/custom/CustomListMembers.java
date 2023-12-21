package com.example.mychat.custom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mychat.R;
import com.squareup.picasso.Picasso;

public class CustomListMembers extends ArrayAdapter {
    Context context;
    String[] names;
    String[] roles;
    String[] avatars;

    public CustomListMembers(@NonNull Context context, int resource, String[] names, String[] roles, String[] avatars) {
        super(context, R.layout.activity_custom_list_members, names);
        this.context = context;
        this.names = names;
        this.roles = roles;
        this.avatars = avatars;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View row=inflater.inflate(R.layout.activity_custom_list_members,null);
        TextView name=(TextView)row.findViewById(R.id.txtName);
        TextView role=(TextView)row.findViewById(R.id.txtRole);
        ImageView avatar=(ImageView)row.findViewById(R.id.iconItem);
        name.setText(names[position]);
        role.setText(roles[position]);
        if (avatars[position] != null) {
            Picasso.get().load(avatars[position]).into(avatar);
        }
        return(row);
    }
}