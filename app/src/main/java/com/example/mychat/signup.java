package com.example.mychat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Spinner spinner = findViewById(R.id.spinnerCountryCode);
//        spinner.setOnItemSelectedListener(this);
//        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        //Setting the ArrayAdapter data on the Spinner
//        spinner.setAdapter(aa);

        EditText editText = findViewById(R.id.editTextPhoneNumber);

        String selectedCountryCode = spinner.getSelectedItem().toString();
        String phoneNumber = editText.getText().toString();

//        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
//        try {
//            // Parse số điện thoại với mã quốc gia
//            Phonenumber.PhoneNumber number = phoneNumberUtil.parse(phoneNumber, selectedCountryCode);
//
//            // Kiểm tra xem số điện thoại có hợp lệ không
//            if (phoneNumberUtil.isValidNumber(number)) {
//                // Số điện thoại hợp lệ
//                String formattedNumber = phoneNumberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.E164);
//                // Sử dụng formattedNumber cho mục đích xử lý tiếp theo
//            } else {
//                // Số điện thoại không hợp lệ
//            }
//        } catch (NumberFormatException e) {
//            // Lỗi xảy ra khi cố gắng phân tích số điện thoại
//            e.printStackTrace();
//        }
    }
}