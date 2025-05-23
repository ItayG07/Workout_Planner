package com.example.workoutplanner;

public class UserDetails {
    private String name;
    private String password;
    private String email;
    private String phoneNumber;
    public UserDetails(String name,String password,String email,String phoneNumber){
        this.name = name;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
    public UserDetails(String name,String password,String email){
        this.name = name;
        this.password = password;
        this.email = email;
    }
    public String toString(){
        return "Name: " + name +" Email: " + email + " Password: " + password + " Phone number: " + phoneNumber;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

}

