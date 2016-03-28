package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by thangjs on 3/26/16.
 */
public class User {

    private String name;
    private Long uid;

    public void setName(String name) {
        this.name = name;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    private String screenName;
    private String profileImageUrl;

    public String getName() {
        return name;
    }

    public Long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromJson(JSONObject json){

        User user = new User();
        try{
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.profileImageUrl = json.getString("profile_image_url");
        }catch (JSONException e){
            e.printStackTrace();
        }

        return user;
    }

}
