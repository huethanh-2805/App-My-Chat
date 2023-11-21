package com.example.mychat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class ChatActivity extends AppCompatActivity{
    ListView listView;
    //
    String[] id; //get id, không hiện lên, để ánh xạ các thuộc tính còn lại
    String[] name; //tên người liên hệ
    String[] string;
    //chuỗi nếu như trong ContactActivity sẽ hiện email,
    // nếu như trong ChatActivity sẽ hiện tin nhắn gần nhất
    Integer[] img; //hình ảnh, ảnh đại diện
    SharedPreferences sharedPreferences;
    AnimatedBottomBar bottomBar;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        listView = (ListView) findViewById(R.id.listView);
        bottomBar = findViewById(R.id.bottom_bar);

        bottomBar.selectTabAt(2,true);
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                if(tab1.getId() == R.id.contact){
                    bottomBar.selectTabAt(i1, true);
                    Intent intent=new Intent(ChatActivity.this, ContactActivity.class);
                    startActivity(intent);
                } else if (tab1.getId() == R.id.more) {
                    bottomBar.selectTabAt(i1, true);
                    Intent intent=new Intent(ChatActivity.this, MoreActivity.class);
                    startActivity(intent);
                } else if (tab1.getId() == R.id.chat) {
                    bottomBar.selectTabAt(i1, true);
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

        applyNightMode();


//        MyArrayAdapter adapter = new MyArrayAdapter(ChatActivity.this, R.layout.array_adapter, name, string, img);
//        listView.setAdapter(adapter);
    }


    private void applyNightMode() {
        sharedPreferences= MyChat.getSharedPreferences();
        boolean nightMode=sharedPreferences.getBoolean("night",false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}


