        package com.codepath.apps.restclienttemplate;

        import android.content.Context;
        import android.content.Intent;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.fragment.app.FragmentManager;
        import androidx.recyclerview.widget.RecyclerView;

        import com.bumptech.glide.Glide;
        import com.codepath.apps.restclienttemplate.Controllers.TimeStampController;
        import com.codepath.apps.restclienttemplate.Controllers.TweetActions;
        import com.codepath.apps.restclienttemplate.Controllers.TwitterClient;
        import com.codepath.apps.restclienttemplate.models.Tweet;
        import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

        import org.jetbrains.annotations.NotNull;
        import org.parceler.Parcels;

        import java.util.List;

        import okhttp3.Headers;

        public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
            Context context;
            List<Tweet> tweets;
            TwitterClient client;

            //Pass in the context and list of tweets.
            public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client) {
                this.context = context;
                this.tweets = tweets;
                this.client = client;

            }

            //For each row, inflate a layout
            @NotNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
                return new ViewHolder(view);
            }

            //Bind values based on the position of the element
            @Override
            public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
                //Get the data at position
                Tweet tweet = tweets.get(position);
                //Bind the tweet with view holder
                holder.bind(tweet);
            }

            @Override
            public int getItemCount() {
                return tweets.size();
            }

            // Clean all elements of the recycler
            public void clear() {
                tweets.clear();
                notifyDataSetChanged();
            }

            // Add a list of items -- change to type used
            public void addAll(List<Tweet> list) {
                tweets.addAll(list);
                notifyDataSetChanged();
            }
            //Define a viewholder (First Step)
            public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
                ImageView ivProfileImage;
                ImageView imgBody1;
                TextView tvBody;
                TextView tvScreenName;
                TextView tvUserName;
                TextView tvTimeStamp;
                Button btnFavorite, btnRetweet, btnReply;
                TextView tvFavorite, tvRetweet;

                public ViewHolder(@NotNull View itemView) {
                    super(itemView);
                    ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
                    imgBody1 = itemView.findViewById(R.id.media);
                    tvBody = itemView.findViewById(R.id.tvBody);
                    tvScreenName = itemView.findViewById(R.id.tvScreenName);
                    tvUserName = itemView.findViewById(R.id.tvUserName);
                    tvTimeStamp = itemView.findViewById(R.id.timeStamp_txt);

                    btnFavorite = itemView.findViewById(R.id.btnFavorite);
                    btnRetweet = itemView.findViewById(R.id.btnRetweet);
                    btnReply = itemView.findViewById(R.id.btnReply);

                    tvFavorite = itemView.findViewById(R.id.tvFavorite);
                    tvRetweet = itemView.findViewById(R.id.tvRetweet);

                    itemView.setOnClickListener(this);
                }

                public void bind(final Tweet tweet) {
                    tvBody.setText(tweet.body);
                    tvScreenName.setText(tweet.user.name);
                    tvUserName.setText("@"+tweet.user.screenName);
                    tvFavorite.setText(String.valueOf(tweet.favorite_count));
                    tvRetweet.setText(String.valueOf(tweet.retweet_count));
                    btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart_stroke);
                    btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet_stroke);

                    Glide.with(context)
                            .load(tweet.user.profileImageUrl)
                            .into(ivProfileImage);
                    tvTimeStamp.setText(TimeStampController.getRelativeTimeAgo(tweet.createdAt));


                    if(!tweet.entities.media.isEmpty())
                    {
                        Glide.with(context)
                                .load(tweet.entities.media.get(0))
                                .into(imgBody1);
                    }
                    else {
                        Glide.with(context).clear(imgBody1);
                    }
                    if (tweet.favorited) {
                        btnFavorite.setBackgroundResource(R.drawable.ic_vector_heart);
                    }
                    if (tweet.retweeted) {
                        btnRetweet.setBackgroundResource(R.drawable.ic_vector_retweet);
                    }


                    btnFavorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TweetActions.LikeTweet(context,tweet,client,btnFavorite,tvFavorite);
                        }
                    });

                    btnRetweet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TweetActions.Retweet(context,tweet,client,btnRetweet,tvRetweet);
                        }
                    });

                    btnReply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TweetActions.ReplyTweet(context,tweet);
                        }
                    });
                }

                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION)
                    {
                        Intent intent = new Intent(context, TweetDetailsActivity.class);
                        Tweet tweet = tweets.get(position);
                        intent.putExtra("tweet", Parcels.wrap(tweet));
                        context.startActivity(intent);
                    }
                }
            }
        }
