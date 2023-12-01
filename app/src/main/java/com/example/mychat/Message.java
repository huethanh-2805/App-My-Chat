package com.example.mychat;


import com.google.firebase.Timestamp;

public class Message {
    private String sender;
    private String receiver;
    private String message;
    private Timestamp timestamp;

    private boolean isAppeared;

    private String type;

    public Message(String sender, String receiver,String message,Timestamp timestamp,String type){
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.isAppeared=false;
        this.timestamp=timestamp;
        this.type=type;
    }

    public Message(){
        this.isAppeared=false;
    }

    public boolean getAppearStatus(){
        return isAppeared;
    }

    public void setAppeared(){
        this.isAppeared=true;
    }

    public void setType(String type){
        this.type=type;
    }

    public String getType(){
        return this.type;
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


    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message=message;
    }


}
