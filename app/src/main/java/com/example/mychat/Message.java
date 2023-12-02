package com.example.mychat;


import com.google.firebase.Timestamp;

public class Message {
    private String sender;
    private String receiver;
    private String sender_delete;
    private String receiver_delete;
    private String message;
    private Timestamp timestamp;

    private boolean isAppeared;


    private String type;



    public Message(String sender, String receiver,String sender_delete, String receiver_delete, String message,Timestamp timestamp, String type){

        this.sender=sender;
        this.receiver=receiver;
        this.sender_delete=sender_delete;
        this.receiver_delete=receiver_delete;
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
    public String getSenderDelete(){
        return sender_delete;
    }

    public void setSenderDelete(String sender_delete){
        this.sender_delete=sender_delete;
    }

    public String getReceiverDelete(){
        return receiver_delete;
    }

    public void setReceiverDelete(String receiver_delete){
        this.receiver_delete=receiver_delete;
    }


    public String getMessage(){
        return message;
    }

    public void setMessage(String message){
        this.message=message;
    }


}
