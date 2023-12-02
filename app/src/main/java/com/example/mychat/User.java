package com.example.mychat;


import com.google.firebase.Timestamp;

public class User {
    private final String name;
    private final String string;
    private final Integer avatarUrl;
    private String uid;
    private final String email;
    Timestamp timestamp;
    public User(String name, String string, Integer img, String email, String uid, Timestamp timestamp) {
        this.name = name;
        this.string = string;
        this.avatarUrl = img;
        this.email=email;
        this.uid=uid;
        this.timestamp=timestamp;
    }



    public String getName(){
        return this.name;
    }
    public String getString(){
        return this.string;
    }
    public Integer getImg(){
        return this.avatarUrl;
    }

    public String getEmail() {return  this.email;}
//    private String uid;
//    private String username;
//
//    private String imageUrl;
//
//    public User(String id, String username,String imageUrl){
//        this.uid=id;
//        this.username=username;
//        this.imageUrl=imageUrl;
//    }
//
//    public User(){};
//
    public String getUid(){
        return this.uid;
    }

    public void setUid(String id){
        this.uid=id;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    //
//    public String getUsername(){
//        return this.username;
//    }
//
//    public void setUsername(String username){
//        this.username=username;
//    }
//
//    public String getImageUrl(){
//        return this.imageUrl;
//    }
//
//    public void setImageUrl(String id){
//        this.imageUrl=imageUrl;
//    }


}
