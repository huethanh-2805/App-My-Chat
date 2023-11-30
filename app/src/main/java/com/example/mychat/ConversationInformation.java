package com.example.mychat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ConversationInformation extends AppCompatActivity {
    String[] items = new String[]{"Change theme", "Media", "Block", "Delete chat"};
    String[] themes = new String[]{"Light Blue", "Nice blue", "Nice green", "Nice Fire", "Nice orange", "Nice pink", "Loso", "Love", "Black heart", "Sweet Chocolate", "Cocacola", "Mochi mochi"};
    Integer[] icons = {R.drawable.ic_theme, R.drawable.ic_picture, R.drawable.ic_block, R.drawable.ic_delete};
    Integer[] colors = {R.drawable.ic_light1, R.drawable.ic_light2, R.drawable.ic_light3, R.drawable.ic_dark1, R.drawable.ic_dark2, R.drawable.ic_dark3, R.drawable.theme3d1, R.drawable.theme_love3d, R.drawable.theme_blackheart, R.drawable.theme_socola, R.drawable.theme_cocacola, R.drawable.theme_mochi};
    ListView listView;
    ListView listViewThemes;
    TextView txtUserName;
    ImageView btn_back;
    Intent intent;
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_information);

        txtUserName = findViewById(R.id.username);
        btn_back = findViewById(R.id.back);
        intent = getIntent();
        String name = intent.getStringExtra("user_name");
        txtUserName.setText(name);

        listView = findViewById(R.id.listView);
        CustomListMore adapter = new CustomListMore(this, R.layout.custom_listview_more, items, icons);
        listView.setAdapter(adapter);

//        CustomListTheme themeAdapter = new CustomListTheme(this, R.layout.custom_list_themes, themes,colors);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showThemeSelectionDialog();
                        break;
                    case 1:
                         break;
                    case 2:
                          break;
                    case 3:
                          break;
                }
            }
        });



        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversationInformation.this, ChatSreen.class);
                ConversationInformation.this.startActivity(intent);
            }
        });

    }

    private void showThemeSelectionDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose a Theme");

            // Set the adapter directly on the AlertDialog.Builder
            builder.setAdapter(new CustomListTheme(this, R.layout.custom_list_themes, themes, colors), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    applyTheme(which);
                    dialog.dismiss();  // Dismiss the dialog after a theme is
                    finish();
                }
            });

            builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error showing dialog", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyTheme(int themeIndex) {
        ThemeHelper.saveSelectedTheme(this, themeIndex);
        recreate(); // Recreate the activity to apply the new theme
    }

}