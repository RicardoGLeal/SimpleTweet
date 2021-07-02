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
    import com.codepath.apps.restclienttemplate.TimelineActivity;
    import com.codepath.apps.restclienttemplate.models.Tweet;
    import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

    import okhttp3.Headers;

    public class TweetActions {

        /**
         * It is responsible for carrying out the function of liking or disliking a tweet. Change the color of the icon and update the value of the number of likes.
         * @param context context
         * @param tweet tweet
         * @param client client
         * @param btnFavorite The button that was pressed
         * @param tvFavorite The textView that shows the number of likes.
         */
        public static void LikeTweet(final Context context, final Tweet tweet, TwitterClient client, final Button btnFavorite, final TextView tvFavorite) {
            if(!tweet.favorited) {
                client.setFavorite(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart);
                        int favorites = Integer.valueOf(tvFavorite.getText().toString()) ;
                        favorites += 1;
                        tvFavorite.setText(String.valueOf(favorites));
                        tweet.favorite_count += 1;
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
            else { //dislike tweet.
                client.unsetFavorite(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                        int favorites = Integer.valueOf(tvFavorite.getText().toString()) ;
                        favorites -= 1;
                        tweet.favorite_count -= 1;
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

        /**
         * is responsible for performing the function of replying to a tweet.
         * It commands to call the compose fragment initializing it with the parameters of the tweet
         * id and the username
         * @param context context
         * @param tweet tweet
         */
        public static void ReplyTweet(final Context context, final Tweet tweet) {
            FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
            ComposeFragment composeFragment = ComposeFragment.newInstance(tweet.id, tweet.user.screenName);
            composeFragment.show(fm, "ComposeTweet");
        }

        /**
         * It is responsible for performing the function of retweeting to a tweet in the TimeLineActivity.
         * Changes the color of the icon and update the value of the number of retweets.
         * @param context context
         * @param tweet tweet
         * @param client client
         * @param btnRetweet button of retweet.
         * @param tvRetweet textView of the number of retweets.
         * @param timelineActivity timelineActivity
         */
        public static void Retweet(final Context context, final Tweet tweet, TwitterClient client, final Button btnRetweet, final TextView tvRetweet, final TimelineActivity timelineActivity) {
                if(!tweet.retweeted) {
                        client.retweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                                int retweets = Integer.valueOf(tvRetweet.getText().toString());
                                retweets += 1;
                                tweet.retweet_count = retweets;
                                tvRetweet.setText(String.valueOf(retweets));
                                tweet.retweeted = true;
                                Toast.makeText(context, "Tweet Retweted!", Toast.LENGTH_SHORT).show();
                                timelineActivity.addTweet(tweet);
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.i("Retweet tweet", "failure" + response);
                            }
                        });
                    }
                }

        /**
         * It is responsible for performing the function of retweeting to a tweet in the TweetDetailsActivity.
         * Changes the color of the icon and update the value of the number of retweets.
         * @param context context
         * @param tweet tweet
         * @param client client
         * @param btnRetweet button of retweet.
         * @param tvRetweet textView of the number of retweets.
         */
        public static void Retweet(final Context context, final Tweet tweet, TwitterClient client, final Button btnRetweet, final TextView tvRetweet) {
            if(!tweet.retweeted) {
                client.retweet(tweet.id, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                        int retweets = Integer.valueOf(tvRetweet.getText().toString());
                        retweets += 1;
                        tweet.retweet_count = retweets;
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
