package com.example.mychat;

public class Chat {
    private String sender;
    private String receiver;
    private String message;

    private String timestamp;

    private boolean isAppeared;

    public Chat(String sender, String receiver,String message){
        this.sender=sender;
        this.receiver=receiver;
        this.message=message;
        this.isAppeared=false;
    }

    public Chat(){
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
