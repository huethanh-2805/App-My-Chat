package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ApperanceActivity extends AppCompatActivity {
    ImageView btnBack;
    RadioGroup checkGroup;
    RadioButton btnLight;
    RadioButton btnDark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apperance);

        btnBack = (ImageView) findViewById(R.id.iconBackMode);
        checkGroup = (RadioGroup) findViewById(R.id.checkBtnGroup);
        btnLight = (RadioButton) findViewById(R.id.light_mode);
        btnDark = (RadioButton) findViewById(R.id.dark_mode);

        //Nút bấm để quay về MoreActivity
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApperanceActivity.this, MoreActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}