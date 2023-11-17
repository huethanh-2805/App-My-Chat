package com.example.mychat;

public class User {
    private String name;
    private String string;
    private Integer img;

    private String email;
    public User(String name, String string, Integer img,String email) {
        this.name = name;
        this.string = string;
        this.img = img;
        this.email=email;
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

}
