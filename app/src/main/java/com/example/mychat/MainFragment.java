package com.example.mychat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class MainFragment extends AppCompatActivity {
    FragmentTransaction ft;
    MoreActivity moreActivity;
    ContactActivity contactActivity;
    ChatActivity chatActivity;
    AnimatedBottomBar bottomBar;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fragment);

        ft = getSupportFragmentManager().beginTransaction();
        chatActivity = ChatActivity.newInstance("init");
        ft.replace(R.id.main_holder, chatActivity);
        ft.commit();

        bottomBar=findViewById(R.id.bottom_bar);

        FirebaseMessaging.getInstance().subscribeToTopic("message")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        String TAG="message";
                        Log.d(TAG, msg);
                        Toast.makeText(MainFragment.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        initBottomBar();

        applyNightMode();
    }

    private void initBottomBar(){
        bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int i, @Nullable AnimatedBottomBar.Tab tab, int i1, @NonNull AnimatedBottomBar.Tab tab1) {
                Fragment selectedFragment = null;

                switch (i1) {
                    case 0:
                        selectedFragment = new ChatActivity();
                        break;
                    case 1:
                        selectedFragment = new ContactActivity();
                        break;
                    case 2:
                        selectedFragment = new MoreActivity();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_holder, selectedFragment)
                            .commit();
                }
            }

            @Override
            public void onTabReselected(int i, @NonNull AnimatedBottomBar.Tab tab) {

            }
        });

    }

    public void changeFragment(Fragment selectedFragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_holder, selectedFragment)
                .commit();
    }

    private void applyNightMode() {
        sharedPreferences=MyChat.getSharedPreferences();
        boolean nightMode=sharedPreferences.getBoolean("night",false);
        if (nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
