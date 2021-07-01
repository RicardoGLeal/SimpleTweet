package com.codepath.apps.restclienttemplate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.codepath.apps.restclienttemplate.Controllers.TwitterClient;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class TimelineActivity extends AppCompatActivity {

    public static final String TAG = "TimelineActivity";
    public final int REQUEST_CODE = 20; //For retrieving the posted tweet.
    private SwipeRefreshLayout swipeContainer;

    TwitterClient client;
    RecyclerView rvTweets;
    List<Tweet> tweets;
    TweetsAdapter adapter;
    FloatingActionButton fabCompose;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        client = TwitterApp.getRestClient(this);
        //Find the recycler view
        rvTweets = findViewById(R.id.rvTweets);
        fabCompose = findViewById(R.id.fabCompose);


        // Sets the Toolbar to act as the ActionBar for this Activity window.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarId);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);




        swipeContainer = findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            fetchTimelineAsync(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //Init the list of tweets and adapter
        tweets = new ArrayList<>();
        adapter = new TweetsAdapter(this, tweets, client, this);
        //Recycler view setup: layout manager and the adapter.
        rvTweets.setLayoutManager(new LinearLayoutManager(this));
        rvTweets.setAdapter(adapter);

        populateHomeTimeline();

        fabCompose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Compose icon has been selected
                //Navigate to the compose activity
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance();
                composeFragment.show(fm, "ComposeTweet");

                //Intent intent = new Intent(this, ComposeActivity.class);
                //startActivityForResult(intent, REQUEST_CODE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       /* if(item.getItemId() == R.id.compose){
            //Compose icon has been selected
            //Navigate to the compose activity
            FragmentManager fm = getSupportFragmentManager();
            ComposeFragment composeFragment = ComposeFragment.newInstance();
            composeFragment.show(fm, "ComposeTweet");

            //Intent intent = new Intent(this, ComposeActivity.class);
            //startActivityForResult(intent, REQUEST_CODE);
            return true;
        }*/
        if(item.getItemId()== R.id.logout_btn){
            client.clearAccessToken();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchTimelineAsync(int page) {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                adapter.clear();
                tweets.clear();
                populateHomeTimeline();
                adapter.addAll(tweets);
                swipeContainer.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("DEBUG", "Fetch timeline error: " + throwable.toString());
            }
        });
    }
    //For retrieving the posted tweet.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            //GET data from the intent (tweet)
            Tweet tweet = Parcels.unwrap(data.getParcelableExtra("tweet"));
            //Update the RV with the tweet.
            //Modify data source of tweets
            tweets.add(0,tweet);
            //Update the adapter
            adapter.notifyItemInserted(0);
            rvTweets.scrollToPosition(0);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateHomeTimeline() {
        client.getHomeTimeline(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.i(TAG, "onSuccess!" + json.toString());
                JSONArray jsonArray = json.jsonArray;;
                try {
                    tweets.addAll(Tweet.fromJsonArray(jsonArray));
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Log.e(TAG, "Json exception", e);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.e(TAG, "onFailure!" + response,throwable);
            }
        });
    }

    public void addTweet(Tweet tweet){
        //Toast.makeText(getApplicationContext(), "Tweet submitted", Toast.LENGTH_LONG).show();
        tweets.add(0, tweet);
        adapter.notifyItemInserted(0);
        scrollToTop();
    }

    public void scrollToTop(){
        rvTweets.smoothScrollToPosition(0);
    }
}