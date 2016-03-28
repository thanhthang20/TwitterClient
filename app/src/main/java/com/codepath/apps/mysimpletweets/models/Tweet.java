package com.codepath.apps.mysimpletweets.models;

import android.content.Context;
import android.util.Log;

import com.codepath.apps.mysimpletweets.TweetsDataBaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thangjs on 3/26/16.
 */
public class Tweet {

    public void setBody(String body) {
        this.body = body;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private String body;
    private Long uid;
    private String createdAt;
    private User user;

    public User getUser() {
        return user;
    }

    public String getBody() {
        return body;
    }

    public Long getUid() {
        return uid;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public static Tweet fromJson(JSONObject jsonObject){
        Tweet tweet =  new Tweet();
        try {
            tweet.body = jsonObject.getString("text");
            tweet.uid = jsonObject.getLong("id");
            tweet.createdAt = jsonObject.getString("created_at");
            tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        }catch (JSONException e){
            e.printStackTrace();
        }

        return tweet;
    }

    public static ArrayList<Tweet> fromJsonArray(Context context, JSONArray jsonArray){
        TweetsDataBaseHelper databaseHelper = TweetsDataBaseHelper.getInstance(context);
        databaseHelper.deleteAllTweetsAndUsers();



        ArrayList<Tweet> tweets = new ArrayList<>();
        for (int i = 0; i<jsonArray.length(); i++){
            try {
                JSONObject tweetObject = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.fromJson(tweetObject);
                if(tweet != null){
                    tweets.add(tweet);
                    databaseHelper.addTweet(tweet);
                }
            }catch (JSONException e){
                e.printStackTrace();
                continue;
            }

        }
        List<Tweet> posts = databaseHelper.getAllTweets();
        Log.d("DEBUG", posts.toString());
        for (Tweet post : posts) {
            // do something

        }
        return tweets;
    }
}
