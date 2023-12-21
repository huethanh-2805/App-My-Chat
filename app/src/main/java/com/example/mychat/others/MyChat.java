package com.example.mychat.others;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class MyChat extends Application {
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        if (!sharedPreferences.contains("night")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Nếu chưa có giá trị "night", đặt giá trị mặc định là false (chế độ sáng)
            editor.putBoolean("night", false);
            editor.apply();
        }
    }

    public static SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }
}
