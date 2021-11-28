package com.binmadhi.motivatdo.FirebaseHelper;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.binmadhi.motivatdo.Models.User;


public class PreferencesManager {
    //shared preference class used to store the current logged in users some required data
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //initialize
    public PreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //save lang data
    public void setLang(String user) {
        editor.putString("LNG", user).commit();
    }  //save user data

    public String getLang() {
        return sharedPreferences.getString("LNG", "en");
    }
    //save user data
    public void setCat(String user) {
        editor.putString("CAT", user).commit();
    }  //save user data

    public String getCat() {
        return sharedPreferences.getString("CAT", "");
    }

    //save user data
    public void setMainCat(String user) {
        editor.putString("MC", user).commit();
    }  //save user data

    public String getMainCat() {
        return sharedPreferences.getString("MC", "");
    }

    //save user data
    public void setTID(String user) {
        editor.putString("TID", user).commit();
    }  //save user data

    public String gettTID() {
        return sharedPreferences.getString("TID", "");
    }

    public void saveCurrentUser(User user) {
        editor.putString("currentUser", new Gson().toJson(user)).commit();
    }

    //return saved data
    public User getCurrentUser() {
        return new Gson().fromJson(sharedPreferences.getString("currentUser", ""), User.class);
    }

    //save the user click for
    public void setLocationCity(String level) {
        editor.putString("location", level).commit();
        editor.apply();
    }


    public String getLatPref() {
        return sharedPreferences.getString("lat", "");
    }

    public String getLocationCity() {
        return sharedPreferences.getString("location", "");
    }

    public String getLangPref() {
        return sharedPreferences.getString("lng", "");
    }

    public void setLocationPref(String lat, String lng) {
        editor.putString("lat", lat).commit();
        editor.putString("lng", lng).commit();
        editor.apply();
    }

    public String getSubject() {
        return sharedPreferences.getString("subject", "");
    }

    public String getLevel() {
        return sharedPreferences.getString("level", "");
    }
}
