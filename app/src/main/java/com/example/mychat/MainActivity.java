package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    String[] items = new String[]{"Account", "Chats", "Apperance", "Notification", "Privacy", "Data Usage", "Help", "Invite Your Friends"};
    Integer[] icons={R.drawable.ic_avt,R.drawable.ic_chats, R.drawable.ic_apperance, R.drawable.ic_noti, R.drawable.ic_privacy, R.drawable.ic_data, R.drawable.ic_help, R.drawable.ic_invite };
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        listView = findViewById(R.id.listView);

        CustomListMore adapter = new CustomListMore(this, R.layout.custom_listview_more, items, icons);
// bind intrinsic ListView to custom adapter
        listView.setAdapter(adapter);
    }

}
