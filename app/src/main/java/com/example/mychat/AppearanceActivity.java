package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AppearanceActivity extends AppCompatActivity {
    ImageView btnBack;
    RadioGroup checkGroup;
    RadioButton btnLight;
    RadioButton btnDark;
    RadioButton btnSystem;
    boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appearance);

        btnBack = (ImageView) findViewById(R.id.iconBackMode);
        checkGroup = (RadioGroup) findViewById(R.id.checkBtnGroup);
        btnLight = (RadioButton) findViewById(R.id.light_mode);
        btnDark = (RadioButton) findViewById(R.id.dark_mode);
        btnSystem = (RadioButton) findViewById(R.id.system_mode);

        //Nút bấm để quay về MoreActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AppearanceActivity.this,MoreActivity.class);
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = MyChat.getSharedPreferences();
        nightMode = sharedPreferences.getBoolean("night", false); //Mặc định chế độ sáng

        if (nightMode){
            btnDark.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        checkGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
//                boolean isRecreate = sharedPreferences.getBoolean("isRecreate", false);

                if (btnLight.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                } else if (btnDark.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor = sharedPreferences.edit();
                    if (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM == AppCompatDelegate.MODE_NIGHT_YES){
                        editor.putBoolean("night", true);
                    } else editor.putBoolean("night", false);
                }

                editor.apply();
//                recreate();

                // Kiểm tra xem có phải là lần đầu chuyển đổi chế độ hay không
//                if (!isRecreate) {
//                    editor.putBoolean("isRecreate", true);
//                    editor.apply();
//                    recreate();
//                } else {
//                    editor.putBoolean("isRecreate", false);
//                    editor.apply();
//                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(AppearanceActivity.this,MoreActivity.class);
        startActivity(intent);
        finish();
    }
}