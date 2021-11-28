package com.binmadhi.motivatdo.Models;

public class TaskModel {
    private String tid,taskName,date,assignTo,rewardName,image, surprise,status,points,type, rewardType;

    public TaskModel(String tid, String taskName, String date, String assignTo, String rewardName, String image, String surprise, String status, String points, String type, String reward) {
        this.tid = tid;
        this.taskName = taskName;
        this.date = date;
        this.assignTo = assignTo;
        this.rewardName = rewardName;
        this.image = image;
        this.surprise = surprise;
        this.status = status;
        this.points = points;
        this.type = type;
        this.rewardType = reward;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TaskModel() {
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAssignTo() {
        return assignTo;
    }

    public void setAssignTo(String assignTo) {
        this.assignTo = assignTo;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSurprise() {
        return surprise;
    }

    public void setSurprise(String surprise) {
        this.surprise = surprise;
    }
}
