package com.example.mychat;


import com.google.firebase.Timestamp;

public class Message {
    private String sender;
    private String receiver;
    private String message;
    private Timestamp timestamp;

    private boolean isAppeared;

    public Message(String sender, String receiver,String message){
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.isAppeared=false;
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
