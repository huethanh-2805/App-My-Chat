package com.example.mychat.others;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeHelper {
    private static final String PREFS_NAME = "MyAppThemePrefs";
    private static final String KEY_THEME = "selected_theme";

    public static void saveSelectedTheme(Context context, int themeIndex) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_THEME, themeIndex);
        editor.apply();
    }

    public static int getSelectedTheme(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_THEME, 0); // Default to the first theme
    }
}

