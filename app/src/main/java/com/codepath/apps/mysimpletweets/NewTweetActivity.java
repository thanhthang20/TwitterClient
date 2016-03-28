package com.codepath.apps.mysimpletweets;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

public class NewTweetActivity extends AppCompatActivity {

    Button btSubmit;
    EditText editText;
    TextView tvCountDown;
    TextView tvError;
    TwitterClient client = TwitterApplication.getRestClient();
    String status;
    int coundown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tweet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btSubmit = (Button) findViewById(R.id.btSubmit);
        editText = (EditText) findViewById(R.id.etNewTweet);
        tvCountDown = (TextView) findViewById(R.id.tvCountDown);
        tvCountDown.setText(Integer.toString(140));
        //tvError = (TextView) findViewById(R.id.tvError);

        editText.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {
                status = editText.getText().toString().trim();
                tvCountDown.setText(Integer.toString(140 - status.length()));
                if(status.length() > 140){

                }
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){}
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status.length() > 140 || status.length() ==0){
                    return;
                }
                client.createNewTweet(status, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d("DEBUG1", Integer.toString(statusCode));
                        finish();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d("DEBUG2", Integer.toString(statusCode));
                        Log.d("DEBUG2", headers.toString());
                        Log.d("DEBUG2", throwable.toString());
                    }
                });
            }
        });
    }

}
