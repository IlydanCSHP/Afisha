package com.diplom.afisha.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.afisha.R;
import com.diplom.afisha.dao.EventDao;
import com.diplom.afisha.dao.UserDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Review;
import com.diplom.afisha.model.User;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    Context context;
    List<Review> reviews;
    Activity activity;
    Boolean isProfile;
    private User user;
    private Event event;
    private static SharedPreferences sPref;

    public ReviewAdapter(Context context, List<Review> reviews, Activity activity, Boolean isProfile) {
        this.context = context;
        this.reviews = reviews;
        this.activity = activity;
        this.isProfile = isProfile;
        sPref = activity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.reviewDate.setText(reviews.get(position).getReviewDate());
        holder.reviewText.setText(reviews.get(position).getReviewText());
        if (reviews.get(position).getReviewRating() == null) {
            holder.reviewRating.setVisibility(View.GONE);
        }
        holder.reviewRating.setText(String.valueOf(reviews.get(position).getReviewRating()));

        if (isProfile) {
            new Thread(() -> {
                EventDao eventDao = AfishaRoomDatabase.getInstance(context).eventDao();
                event = eventDao.findById(reviews.get(position).getEventId());
                setReviewTitle(holder, event.getTitle());
            }).start();
        } else {
            new Thread(() -> {
                UserDao userDao = AfishaRoomDatabase.getInstance(context).userDao();
                user = userDao.findById(reviews.get(position).getUserId());
                setReviewTitle(holder, user.getUsername());
            }).start();
        }
    }

    private void setReviewTitle(@NonNull ReviewViewHolder holder, String user) {
        activity.runOnUiThread(() -> {
            holder.reviewUser.setText(user);
        });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView reviewUser;
        TextView reviewDate;
        TextView reviewRating;
        TextView reviewText;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewUser = itemView.findViewById(R.id.review_user);
            reviewDate = itemView.findViewById(R.id.ticket_date);
            reviewRating = itemView.findViewById(R.id.review_rating);
            reviewText = itemView.findViewById(R.id.review_text);

            if (sPref.getBoolean("isAdmin", false)) {
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(), 0, 0, "Удалить");
        }
    }
}
