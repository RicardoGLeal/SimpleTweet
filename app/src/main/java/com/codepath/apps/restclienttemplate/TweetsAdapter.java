package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{
    Context context;
    List<Tweet> tweets;

    //Pass in the context and list of tweets.
    public TweetsAdapter(Context context, List<Tweet> tweets) {
        this.context = context;
        this.tweets = tweets;
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
        TextView tvFavorite, tvRetweet, tvReply;

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

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText(tweet.user.name);
            tvUserName.setText("@"+tweet.user.screenName);
            tvFavorite.setText(String.valueOf(tweet.favorite_count));
            tvRetweet.setText(String.valueOf(tweet.retweet_count));

            Glide.with(context)
                    .load(tweet.user.profileImageUrl)
                    .into(ivProfileImage);
            tvTimeStamp.setText(getRelativeTimeAgo(tweet.createdAt));
            if(!tweet.entities.media.isEmpty())
            {
                Glide.with(context)
                        .load(tweet.entities.media.get(0))
                        .into(imgBody1);
            }
            else {
                Glide.with(context).clear(imgBody1);
            }
        }

        // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            String relativeDate = "";
            try {
                long dateMillis = sf.parse(rawJsonDate).getTime();
                relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                        System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            return relativeDate;
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Clicked!", Toast.LENGTH_SHORT).show();
        }
    }
}
