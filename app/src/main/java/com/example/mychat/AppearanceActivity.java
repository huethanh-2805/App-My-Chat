package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
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
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false); //Mặc định chế độ sáng

        if (nightMode){
            btnDark.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        checkGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                //Mặc định chế độ light
                //Khi chọn chế độ sáng
                if (btnLight.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                }
                //Khi chọn chế độ tối
                else if (btnDark.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                //Khi chọn theo chế độ của hệ thống
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor = sharedPreferences.edit();
                    if (AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM == AppCompatDelegate.MODE_NIGHT_YES){
                        editor.putBoolean("night", true);
                    } else editor.putBoolean("night", false);
                }

                editor.apply();
            }
        });
    }


}