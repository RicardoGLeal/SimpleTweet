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
    import android.widget.Toast;

    import androidx.annotation.Nullable;
    import androidx.fragment.app.DialogFragment;

    import com.codepath.apps.restclienttemplate.Controllers.TwitterClient;
    import com.codepath.apps.restclienttemplate.models.Tweet;
    import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
    import com.google.android.material.textfield.TextInputLayout;

    import org.json.JSONException;
    import org.parceler.Parcels;

    import okhttp3.Headers;

    public class ComposeFragment extends DialogFragment {

        public static final String TAG = "ComposeActivity";
        public static final int MAX_TWEET_LENGTH = 280;
        EditText etCompose;
        Button btnTweet;
        TextInputLayout textInputLayout;
        TwitterClient client;

        public ComposeFragment() {
        }

        public static ComposeFragment newInstance() {
            ComposeFragment frag = new ComposeFragment();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        public static ComposeFragment newInstance(long tweetId, String username) {
            ComposeFragment frag = new ComposeFragment();
            Bundle args = new Bundle();
            args.putLong("tweetId", tweetId);
            args.putString("username",username);
            frag.setArguments(args);
            return frag;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.activity_compose, container);
        }

        @Override
        public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Get field from view
            client = TwitterApp.getRestClient(getContext());
            etCompose = view.findViewById(R.id.etCompose);
            btnTweet = view.findViewById(R.id.btnTweet);
            textInputLayout = view.findViewById(R.id.textInputLayout);
            textInputLayout.setCounterEnabled(true);
            textInputLayout.setCounterMaxLength(MAX_TWEET_LENGTH);

            // Show soft keyboard automatically and request focus to field

            getDialog().getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            final String username = getArguments().getString("username", "empty");
            final Long tweetid = getArguments().getLong("tweetId", 0);

            if(username != "empty") {
                etCompose.setText("@"+username);
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
                    Toast.makeText(getContext(),tweetContent,Toast.LENGTH_SHORT).show();
                    //Make an API call to Twitter to publish the tweet.
                    if(username != "empty" && tweetid != 0) {
                        client.replyToTweet(tweetid, tweetContent, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess replying to the tweet");
                                Toast.makeText(getContext(), "onSuccess replying to the tweet!", Toast.LENGTH_SHORT).show();

                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    Intent intent = new Intent();
                                    intent.putExtra("tweet", Parcels.wrap(tweet));
                                    //set result code and bundle data for response
                                    //setResult(RESULT_OK, intent);
                                    //closes the activity, pass data to parent
                                    dismiss();
                                    //finish();
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
                    else {
                        client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i(TAG, "onSuccess to publish tweet");
                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    Intent intent = new Intent();
                                    intent.putExtra("tweet", Parcels.wrap(tweet));
                                    //set result code and bundle data for response
                                    //setResult(RESULT_OK, intent);
                                    //closes the activity, pass data to parent
                                    dismiss();
                                    //finish();
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
