package com.example.mychat.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.mychat.R;
import com.example.mychat.object.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
public class NewGroupAdapter extends ArrayAdapter<User> implements Filterable {
    Context context;
    List<User> user=new ArrayList<>();
    List<User> userOld=new ArrayList<>();
    List<User> userSearch;

    CheckBox checkUser;

    public NewGroupAdapter(Context context, int layoutToBeInflated, List<User> user) {
        super(context, R.layout.array_adapter, user);
        this.context = context;
        this.user = user;
        this.userOld=new ArrayList<>(user);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=((Activity)context).getLayoutInflater();
        View row=inflater.inflate(R.layout.adapter_new_group,null);
        TextView txtName=(TextView)row.findViewById(R.id.txtName);
        TextView txtString=(TextView)row.findViewById(R.id.txtString);
        CircleImageView imgView =(CircleImageView) row.findViewById(R.id.imgView);
        final CheckBox checkUser = row.findViewById(R.id.checkUser);

        final User currentUser = user.get(position);
        txtName.setText(currentUser.getName());
        txtString.setText(currentUser.getEmail());
        if (currentUser.getImg() != null) {
            Picasso.get().load(currentUser.getImg()).into(imgView);
        }
        // Set trạng thái checkbox dựa vào thuộc tính isChecked của đối tượng User
        checkUser.setChecked(currentUser.isChecked());

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi click vào item, cập nhật trạng thái của checkbox và thông báo cho adapter
                currentUser.setChecked(!currentUser.isChecked());
                updateCheckboxState(position, currentUser.isChecked());
//                Toast.makeText(context.getApplicationContext(),String.valueOf(position),Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }

    public void updateCheckboxState(int position, boolean isChecked) {
        // Đảm bảo position hợp lệ
        if (position >= 0 && position < user.size()) {
            User item = user.get(position);
            if (item != null) {
                item.setChecked(isChecked);
                notifyDataSetChanged(); // Thông báo cho adapter biết rằng dữ liệu đã thay đổi
            }
        }
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
