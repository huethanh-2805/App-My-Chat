package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class signup extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {
    String[] code = { "+84", "+90", "+1"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Spinner spinner = findViewById(R.id.spinnerCountryCode);
        spinner.setOnItemSelectedListener(this);
        //EditText editText = findViewById(R.id.editTextPhoneNumber);

//        String selectedCountryCode = spinner.getSelectedItem().toString();
//        String phoneNumber = editText.getText().toString();
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,code);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}