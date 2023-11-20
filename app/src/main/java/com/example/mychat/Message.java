package com.example.mychat;


import com.google.firebase.Timestamp;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private Timestamp timestamp;

    public Message(String sender, String receiver, String message, Timestamp timestamp){
        this.sender=sender;
        this.receiver=receiver;
        this.content=message;
        this.timestamp=timestamp;
    }

    public Message(){

    }

    public String getSender(){
        return sender;
    }

    public void setSender(String sender){
        this.sender=sender;
    }

    public String getReceiver(){
        return receiver;
    }

    public void setReceiver(String receiver){
        this.receiver=receiver;
    }


    public String getContent(){
        return content;
    }

    public void setMessage(String message){
        this.content=message;
    }


}
