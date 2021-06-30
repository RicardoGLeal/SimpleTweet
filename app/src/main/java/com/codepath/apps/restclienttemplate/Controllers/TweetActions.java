    package com.codepath.apps.restclienttemplate.Controllers;

    import android.content.Context;
    import android.util.Log;
    import android.view.View;
    import android.widget.Button;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.FragmentManager;

    import com.codepath.apps.restclienttemplate.ComposeFragment;
    import com.codepath.apps.restclienttemplate.R;
    import com.codepath.apps.restclienttemplate.models.Tweet;
    import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

    import okhttp3.Headers;

    public class TweetActions {

        public static void LikeTweet(final Context context, final Tweet tweet, TwitterClient client, final Button btnFavorite, final TextView tvFavorite) {
            if(!tweet.favorited) {
                client.setFavorite(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart);
                        int favorites = Integer.valueOf(tvFavorite.getText().toString()) ;
                        favorites += 1;
                        tvFavorite.setText(String.valueOf(favorites));
                        tweet.favorited = true;
                        Toast.makeText(context, "Tweet Liked!", Toast.LENGTH_SHORT).show();
                        Log.i("Like tweet", "success");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.i("Like tweet", "failure" + response);
                    }
                });
            }
            else {
                client.unsetFavorite(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                        int favorites = Integer.valueOf(tvFavorite.getText().toString()) ;
                        favorites -= 1;
                        tvFavorite.setText(String.valueOf(favorites));
                        tweet.favorited = false;
                        Toast.makeText(context, "Tweet Disliked!", Toast.LENGTH_SHORT).show();
                        Log.i("Unliked tweet", "success");
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

                    }
                });
            }
        }
        public static void ReplyTweet(final Context context, final Tweet tweet) {
            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            ComposeFragment composeFragment = ComposeFragment.newInstance(tweet.id, tweet.user.screenName);
            composeFragment.show(fm, "ComposeTweet");
        }

        public static void Retweet(final Context context, final Tweet tweet, TwitterClient client, final Button btnRetweet, final TextView tvRetweet) {
                    if(!tweet.retweeted) {
                        client.retweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                                int retweets = Integer.valueOf(tvRetweet.getText().toString());
                                retweets += 1;
                                tvRetweet.setText(String.valueOf(retweets));
                                tweet.retweeted = true;
                                Toast.makeText(context, "Tweet Retweted!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("Retweet tweet", "failure" + response);
                            }
                        });
                    }
                }

    }
