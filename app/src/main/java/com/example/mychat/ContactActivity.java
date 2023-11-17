package com.example.mychat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

public class ContactActivity extends Activity {
    Button btnNewContact, btnChat;
    ListView listView;
    //
    String[] id; //get id, không hiện lên, để ánh xạ các thuộc tính còn lại
    String[] name=new String[]{"Thanh Nam","Thuy Huong","Tri Nhan","Thanh Hue","Anh Thu"}; //tên người liên hệ
    String[] string=new String[]{"a","b","c","d","e"};
    //chuỗi nếu như trong ContactActivity sẽ hiện email,
    // nếu như trong ChatActivity sẽ hiện tin nhắn gần nhất
    Integer[] img={R.drawable.ic_avt,R.drawable.ic_avt,R.drawable.ic_avt,R.drawable.ic_avt,R.drawable.ic_avt}; //hình ảnh, ảnh đại diện
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        listView = (ListView) findViewById(R.id.listView);
        btnNewContact = (Button) findViewById(R.id.btnNewContact);
        btnChat = (Button) findViewById(R.id.btnChat);

        MyArrayAdapter adapter = new MyArrayAdapter(ContactActivity.this, R.layout.array_adapter, name, string, img);
        listView.setAdapter(adapter);
        listView.setSelection(0);
        listView.smoothScrollToPosition(0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(ContactActivity.this,ChatSreen.class);
                intent.putExtra("userid",name[position]);
                ContactActivity.this.startActivity(intent);
            }
        });
    }
}


