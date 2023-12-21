package com.example.mychat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.mychat.others.MyChat;
import com.example.mychat.R;
import com.example.mychat.fragment.MainFragment;

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
                editor = sharedPreferences.edit();
                if (btnLight.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("night", false);

                } else if (btnDark.isChecked()) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("night", true);
                } else {
                    int mode=setNightMode(getApplicationContext(),isSystemInNightMode(getApplicationContext()));
                    editor.putBoolean("night", mode == AppCompatDelegate.MODE_NIGHT_YES);
                }
                editor.apply();
            }
        });

        //Nút bấm để quay về MoreActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AppearanceActivity.this, MainFragment.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent=new Intent(AppearanceActivity.this,ChatActivity.class);
//        startActivity(intent);
//        finish();
//    }
    public static int setNightMode(Context context, boolean isNightMode) {
        int mode = isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);
        return mode;
    }
    public static boolean isSystemInNightMode(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        if (uiModeManager != null) {
            return (uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES);
        }
        return false;
    }
}