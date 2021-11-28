package com.binmadhi.motivatdo.Models;

public class User {
    private String uid, name, email, phoneNo, image, type, familyName,date,points,celebrate,createdBy;

    public User(String uid, String name, String email, String phoneNo, String image, String type, String familyName, String date, String points, String celebrate, String createdBy) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNo = phoneNo;
        this.image = image;
        this.type = type;
        this.familyName = familyName;
        this.date = date;
        this.points = points;
        this.celebrate = celebrate;
        this.createdBy = createdBy;
    }


    public String getCelebrate() {
        return celebrate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCelebrate(String celebrate) {
        this.celebrate = celebrate;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User() {

    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    } //test

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
