package com.example.mychat;

public class User {
    private String uid;
    private String username;

    private String imageUrl;

    public User(String id, String username,String imageUrl){
        this.uid=id;
        this.username=username;
        this.imageUrl=imageUrl;
    }

    public User(){};

    public String getId(){
        return this.uid;
    }

    public void setId(String id){
        this.uid=id;
    }

    public String getUsername(){
        return this.username;
    }

    public void setUsername(String username){
        this.username=username;
    }

    public String getImageUrl(){
        return this.imageUrl;
    }

    public void setImageUrl(String id){
        this.imageUrl=imageUrl;
    }


}
