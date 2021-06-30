package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.Controllers.TimeStampController;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.parceler.Parcels;

public class TweetDetailsActivity extends AppCompatActivity {
    TextView tvBody;
    ImageView ivProfileImage;
    ImageView media;
    TextView tvScreenName;
    TextView tvUserName;
    TextView tvTimeStamp;
    Button btnFavorite, btnRetweet, btnReply;
    TextView tvFavorite, tvRetweet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_details);

        tvBody = findViewById(R.id.tvBody_details);
        ivProfileImage = findViewById(R.id.ivProfileImage_details);
        media = findViewById(R.id.media_details);
        tvScreenName = findViewById(R.id.tvScreenName_details);
        tvUserName = findViewById(R.id.tvUserName_details);
        tvTimeStamp = findViewById(R.id.timeStamp_txt_details);
        btnFavorite = findViewById(R.id.btnFavorite_details);
        btnReply = findViewById(R.id.btnReply_details);
        btnRetweet = findViewById(R.id.btnRetweet_details);
        tvFavorite = findViewById(R.id.tvFavorite_details);
        tvRetweet = findViewById(R.id.tvRetweet_details);

        Intent intent = getIntent();
        final Tweet tweet = Parcels.unwrap(intent.getParcelableExtra("tweet"));
        loadResources(tweet);
    }

    private void loadResources(final Tweet tweet) {
        tvBody.setText(tweet.body);
        Glide.with(getBaseContext())
                .load(tweet.user.profileImageUrl)
                .into(ivProfileImage);
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

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                ComposeFragment composeFragment = ComposeFragment.newInstance(tweet.id, tweet.user.screenName);
                composeFragment.show(fm, "ComposeTweet");
            }
        });


    }
}