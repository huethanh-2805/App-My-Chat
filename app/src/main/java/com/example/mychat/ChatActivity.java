package com.example.mychat;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ChatActivity extends Activity {
    Button btnMore, btnChat;
    ListView listView;
    //
    String[] id; //get id, không hiện lên, để ánh xạ các thuộc tính còn lại
    String[] name; //tên người liên hệ
    String[] string;
    //chuỗi nếu như trong ContactActivity sẽ hiện email,
    // nếu như trong ChatActivity sẽ hiện tin nhắn gần nhất
    Integer[] img; //hình ảnh, ảnh đại diện
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = (ListView) findViewById(R.id.listView);
        btnMore = (Button) findViewById(R.id.btnMore);
        btnChat = (Button) findViewById(R.id.btnChat);

        MyArrayAdapter adapter = new MyArrayAdapter(ChatActivity.this, R.layout.array_adapter, name, string, img);
        listView.setAdapter(adapter);
    }
}


