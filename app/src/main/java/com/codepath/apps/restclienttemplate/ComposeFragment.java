    package com.codepath.apps.restclienttemplate;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.view.WindowManager;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.Nullable;
    import androidx.fragment.app.DialogFragment;

    import com.codepath.apps.restclienttemplate.Controllers.TwitterClient;
    import com.codepath.apps.restclienttemplate.models.Tweet;
    import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
    import com.google.android.material.textfield.TextInputLayout;

    import org.json.JSONException;
    import org.parceler.Parcels;
    import org.w3c.dom.Text;

    import okhttp3.Headers;


    public class ComposeFragment extends DialogFragment {

        public static final String TAG = "ComposeActivity";
        public static final int MAX_TWEET_LENGTH = 280;
        EditText etCompose;
        Button btnTweet;
        TextInputLayout textInputLayout;
        TextView tvreplyTo;
        TwitterClient client;
        Boolean isReply;

        /**
         * Empty constructor needed to create fragments.
         */
        public ComposeFragment() {
        }

        /**
         * Method used to instantiate a tweet when composing a new tweet
         * @return ComposeFragment
         */
        public static ComposeFragment newInstance() {
            ComposeFragment frag = new ComposeFragment();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        /**
         * Method used to instantiate a tweet when replying to a tweet
         * @param tweetId id of the tweet
         * @param username tweet owner username
         * @return ComposeFragment
         */
        public static ComposeFragment newInstance(long tweetId, String username) {
            ComposeFragment frag = new ComposeFragment();
            Bundle args = new Bundle();
            args.putLong("tweetId", tweetId);
            args.putString("username",username);
            frag.setArguments(args);
            return frag;
        }

        /**
         * Inflates the layout.
         * @param inflater
         * @param container
         * @param savedInstanceState
         * @return
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_compose, container);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Get fields from view
            client = TwitterApp.getRestClient(getContext());
            etCompose = view.findViewById(R.id.etCompose);
            btnTweet = view.findViewById(R.id.btnTweet);
            tvreplyTo = view.findViewById(R.id.replyingToId);

            textInputLayout = view.findViewById(R.id.textInputLayout);
            textInputLayout.setCounterEnabled(true);
            textInputLayout.setCounterMaxLength(MAX_TWEET_LENGTH);

            // Show soft keyboard automatically and request focus to field
            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            //Get the username and tweetid passed from the activity.
            final String username = getArguments().getString("username", "empty");
            final Long tweetid = getArguments().getLong("tweetId", 0);


            if(username != "empty" && tweetid != 0) {
                isReply = true;
            } else {
                isReply = false;
            }
                if(isReply) {
                tvreplyTo.setVisibility(View.VISIBLE);
                tvreplyTo.setText("Replying to @"+username);
            } else {
                tvreplyTo.setVisibility(View.GONE);
            }

            etCompose.requestFocus();

            //Set click listener on button
            btnTweet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tweetContent = etCompose.getText().toString();
                    if(tweetContent.isEmpty()) {
                        Toast.makeText(getContext(), "Sorry, your tweet cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(tweetContent.length() > MAX_TWEET_LENGTH) {
                        Toast.makeText(getContext(), "Sorry, your tweet is too long", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //If the tweet is a reply...
                    if(isReply) {
                        tweetContent = "@"+username+" "+etCompose.getText().toString();
                        //calls an endpoint of the API to reply to that tweet.
                        client.replyToTweet(tweetid, tweetContent, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess replying to the tweet");
                                Toast.makeText(getContext(), "onSuccess replying to the tweet!", Toast.LENGTH_SHORT).show();
                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    ((TimelineActivity)getActivity()).addTweet(tweet);
                                    //closes the activity, pass data to parent
                                    dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure to reply to that tweet", throwable);
                                Toast.makeText(getContext(), "onFailure to reply to that tweet", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else { //The tweet is not a reply, is a new tweet.
                        //Calls an endpoint from the API call to publish the tweet.
                        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess to publish tweet");
                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    ((TimelineActivity)getActivity()).addTweet(tweet);
                                    //closes the activity, pass data to parent
                                    dismiss();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e(TAG, "onFailure to publish tweet", throwable);
                            }
                        });
                    }


                }
            });
        }
    }
