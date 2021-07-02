package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.Controllers.TimeStampController;
import com.codepath.apps.restclienttemplate.Controllers.TweetActions;
import com.codepath.apps.restclienttemplate.Controllers.TwitterClient;
import com.codepath.apps.restclienttemplate.databinding.ActivityTweetDetailsBinding;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {
    ActivityTweetDetailsBinding binding;
    TextView tvBody;
    ImageView ivProfileImage;
    ImageView media;
    TextView tvScreenName;
    TextView tvUserName;
    TextView tvTimeStamp;
    Button btnFavorite, btnRetweet, btnReply;
    TextView tvFavorite, tvRetweet;
    TwitterClient client;
    Context context;

    /**
     * Load the activity and find all the references of the objects.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTweetDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //the values of the resources are established applying the View Binding library
        tvBody = binding.tvBodyDetails;
        ivProfileImage = binding.ivProfileImageDetails;
        media = binding.mediaDetails;
        tvScreenName = binding.tvScreenNameDetails;
        tvUserName = binding.tvUserNameDetails;
        tvTimeStamp = binding.timeStampTxtDetails;
        btnFavorite = binding.btnFavoriteDetails;
        btnReply = binding.btnReplyDetails;
        btnRetweet = binding.btnRetweetDetails;
        tvFavorite = binding.tvFavoriteDetails;
        tvRetweet = binding.tvRetweetDetails;

        client = TwitterApp.getRestClient(getBaseContext());

        context = getBaseContext();

        //Receives the tweet that was sent from TimelineActivity.
        Intent intent = getIntent();
        final Tweet tweet = Parcels.unwrap(intent.getParcelableExtra("tweet"));

        Toolbar toolbar = binding.toolbarId;
        toolbar.setTitle("Tweet");
        setSupportActionBar(toolbar);
        loadResources(tweet);
    }

    /**
     * This functions loads all the information of the tweet received.
     * @param tweet the tweet that is being worked on
     */
    private void loadResources(final Tweet tweet) {
        tvBody.setText(tweet.body);

        //set the initial icons of the buttons.
        btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
        btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);

        //if the tweet was found to be marked as favorite, the icon is changed.
        if (tweet.favorited) {
            btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart);
        }
        //if the tweet was found to be retweeted, the icon is changed.
        if (tweet.retweeted) {
            btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
        }

        //user image is loaded implementing Glide.
        Glide.with(getBaseContext())
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImage);

        //if the tweet has an embedded image, it is loaded implementing Glide.
        if(!tweet.entities.media.isEmpty())
        {
            Glide.with(getBaseContext())
                    .load(tweet.entities.media.get(0))
                    .into(media);
        }
        tvScreenName.setText(tweet.user.screenName);
        tvUserName.setText("@"+tweet.user.name);
        tvTimeStamp.setText(TimeStampController.getDate(tweet.createdAt));
        tvFavorite.setText(String.valueOf(tweet.favorite_count));
        tvRetweet.setText(String.valueOf(tweet.retweet_count));

        //onClickLister of the Reply Button
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TweetActions.ReplyTweet(getApplicationContext(), tweet);
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance(tweet.id, tweet.user.screenName);
                composeFragment.show(fm, "ComposeTweet");
            }
        });

        //onClickLister of the Favorite Button
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetActions.LikeTweet(context,tweet,client,btnFavorite,tvFavorite);
            }
        });

        //onClickLister of the Retweet Button
        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TweetActions.Retweet(context,tweet,client,btnRetweet,tvRetweet);
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }
}