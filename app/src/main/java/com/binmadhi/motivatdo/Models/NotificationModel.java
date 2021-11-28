package com.binmadhi.motivatdo.Models;

public class NotificationModel {
    private String nid,notificationTitle,uid,task_id,message,image;

    public NotificationModel(String nid, String notificationTitle, String uid, String task_id, String message, String image) {
        this.nid = nid;
        this.notificationTitle = notificationTitle;
        this.uid = uid;
        this.task_id = task_id;
        this.message = message;
        this.image = image;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public NotificationModel() {
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
