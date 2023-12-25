package com.example.mychat.iface;

import com.example.mychat.object.Message;

public interface OnItemClickListener {
    void onItemClick(Message mess);
    void onItemClickForward(Message mess,String type,String title);


}
