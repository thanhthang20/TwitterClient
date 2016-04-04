package com.codepath.apps.mysimpletweets;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    TwitterClient client;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        client = TwitterApplication.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                user = User.fromJson(response);
                getSupportActionBar().setTitle(user.getScreenName());
                populateProfileHeader(user);
            }
        });

        String screenname = getIntent().getStringExtra("screenname");

        if(savedInstanceState == null){
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(screenname);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flContainer, fragmentUserTimeline);
            ft.commit();
        }

    }

    public void populateProfileHeader(User user){
        TextView tvName = (TextView) findViewById(R.id.tvName);
        ImageView ivAvatar = (ImageView) findViewById(R.id.ivProfileImage);
        TextView tvFollowers = (TextView) findViewById(R.id.tvFollowers);
        TextView tvFollowing = (TextView) findViewById(R.id.tvFollowing);
        TextView tvTagline = (TextView) findViewById(R.id.tvTagline);

        tvFollowers.setText(Integer.toString(user.getFollowersCounts())+ " followers");
        tvFollowing.setText(Integer.toString(user.getFollowingsCounts()) + " followings");
        tvName.setText(user.getName());
        tvTagline.setText(user.getTagline());
        Picasso.with(this).load(user.getProfileImageUrl()).into(ivAvatar);

    }

}
