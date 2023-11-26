package com.example.mychat;

public class User {
    private final String name;
    private final String string;
    private final Integer img;
    private String uid;
    private final String email;
    public User(String name, String string, Integer img,String email, String uid) {
        this.name = name;
        this.string = string;
        this.img = img;
        this.email=email;
        this.uid=uid;
    }



    public String getName(){
        return this.name;
    }
    public String getString(){
        return this.string;
    }
    public Integer getImg(){
        return this.img;
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
