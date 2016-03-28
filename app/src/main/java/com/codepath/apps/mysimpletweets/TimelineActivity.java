package com.codepath.apps.mysimpletweets;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class TimelineActivity extends AppCompatActivity {

    TwitterClient client;
    ArrayList<Tweet> tweets;
    TweetsArrayAdapter aTweets;
    ListView lvTweets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtivity_timeline);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = TwitterApplication.getRestClient();
        lvTweets = (ListView) findViewById(R.id.lvTweets);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(this, tweets);
        lvTweets.setAdapter(aTweets);


        populateTimeline();


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.new_tweet) {

            Intent i = new Intent(TimelineActivity.this, NewTweetActivity.class);
            // put "extras" into the bundle for access in the second activity
            i.putExtra("username", "foobar");
            i.putExtra("in_reply_to", "george");
            i.putExtra("code", 400);
            // brings up the second activity
            startActivityForResult(i, 1);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public  void populateTimeline(){
        aTweets.clear();
        client.getHomeTimeline(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                //super.onSuccess(statusCode, headers, response);
                Log.d("DEBUG", response.toString());
                aTweets.addAll(Tweet.fromJsonArray(getBaseContext(), response));


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (requestCode == 1) {
            populateTimeline();
        }
    }

}
