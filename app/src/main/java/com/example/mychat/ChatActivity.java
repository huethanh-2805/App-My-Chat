package com.example.mychat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class ChatActivity extends Fragment {
    ListView listView;
    //
    String[] id; //get id, không hiện lên, để ánh xạ các thuộc tính còn lại
    String[] name; //tên người liên hệ
    String[] string;
    //chuỗi nếu như trong ContactActivity sẽ hiện email,
    // nếu như trong ChatActivity sẽ hiện tin nhắn gần nhất
    Integer[] img; //hình ảnh, ảnh đại diện
    SharedPreferences sharedPreferences;

    Context context;
    MainFragment mainFragment;

    @SuppressLint("MissingInflatedId")
    public static ChatActivity newInstance(String strArg) {
        ChatActivity fragment = new ChatActivity();
        Bundle args = new Bundle();
        args.putString("ChatActivity", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity(); // use this reference to invoke main callbacks
            mainFragment = (MainFragment) getActivity();
        } catch (IllegalStateException e) {
            throw new IllegalStateException("MainFragment must implement callbacks");
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        LinearLayout layout_chat = (LinearLayout) inflater.inflate(R.layout.activity_chat, null);
        listView = (ListView) layout_chat.findViewById(R.id.listView);


//        MyArrayAdapter adapter = new MyArrayAdapter(ChatActivity.this, R.layout.array_adapter, name, string, img);
//        listView.setAdapter(adapter);
        return layout_chat;
    }
}


