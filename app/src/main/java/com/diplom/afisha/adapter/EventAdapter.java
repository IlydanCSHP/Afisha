package com.diplom.afisha.adapter;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.afisha.EventActivity;
import com.diplom.afisha.R;
import com.diplom.afisha.dao.ReviewDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Review;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    Context context;
    List<Event> events;
    Activity activity;
    List<Review> reviews = new ArrayList<>();
    Double eventRating = 0.0;
    ReviewAdapter reviewAdapter;
    Review review;

    private ReviewDao reviewDao;
    private static SharedPreferences sPref;

    public EventAdapter() {

    }

    public EventAdapter(Context context, List<Event> events, Activity activity) {
        this.context = context;
        this.events = events;
        this.activity = activity;
        sPref = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.event_item, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        holder.eventTitle.setText(events.get(position).getTitle());
        holder.eventDescription.setText(events.get(position).getDescription());

        RecyclerView.LayoutManager manager = new LinearLayoutManager(context);
        holder.reviewRecycler.setLayoutManager(manager);
        new Thread(() -> {
            reviewDao = AfishaRoomDatabase.getInstance(context).reviewDao();
            reviews = reviewDao.findByEventId(events.get(position).getId());
            eventRating = reviewDao.getEventRating(events.get(position).getId());
            if (reviews.size() > 0) {
                review = reviews.get(reviews.size() - 1);
            }
            if (review == null) {
                review = new Review("", "", events.get(position).getId(), 0L, null);
            }
            reviews.clear();
            reviews.add(review);
            if (eventRating != null) {
                setLatestReview(holder, reviews, eventRating);
            }
        }).start();
        holder.itemView.setOnClickListener(view -> {
            openReviews(holder, position);
        });
        holder.reviewsButton.setOnClickListener(view -> {
            openReviews(holder, position);
        });
        if (!sPref.getBoolean("isAdmin", false) && sPref.getBoolean("isSignedIn", false)) {
            final SharedPreferences sPref = context.getSharedPreferences("bookmarked_events", Context.MODE_PRIVATE);
            holder.bookmarkButton.setOnClickListener(view -> {
                if (events.get(position).getId() != sPref.getLong("event_id_" + events.get(position).getId(), 0L)) {
                    holder.bookmarkButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bookmark_filled));
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putLong("event_id_" + events.get(position).getId(), events.get(position).getId());
                    Log.d(TAG, "onBindViewHolder: " + events.get(position).getId());
                    ed.apply();
                    Toast.makeText(context, "Добавлено в закладки!", Toast.LENGTH_SHORT).show();
                } else {
                    holder.bookmarkButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bookmark_icon));
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.remove("event_id_" + events.get(position).getId());
                    ed.apply();
                    Toast.makeText(context, "Удалено из закладок!", Toast.LENGTH_SHORT).show();
                }
            });
            sPref.getAll().forEach((key, value) -> {
                if (events.get(position).getId() == value) {
                    holder.bookmarkButton.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.bookmark_filled));
                }
            });
        }

    }

    private void setLatestReview(@NonNull EventViewHolder holder, List<Review> reviews, Double eventRating) {
        activity.runOnUiThread(() -> {
            holder.eventRating.setText(String.format("%.1f", eventRating));
            holder.eventRating.setCompoundDrawablesWithIntrinsicBounds(
                    AppCompatResources.getDrawable(context, R.drawable.rate_icon),
                    null, null, null);
            reviewAdapter = new ReviewAdapter(context, reviews, activity, false);
            holder.reviewRecycler.setAdapter(reviewAdapter);
        });
    }

    private void openReviews(@NonNull EventViewHolder holder, int position) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra("event_title", holder.eventTitle.getText());
        intent.putExtra("event_description", holder.eventDescription.getText());
        intent.putExtra("event_id", events.get(position).getId());
        Log.d(TAG, "onBindViewHolder: " + events.get(position).getId());
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView eventTitle;
        TextView eventDescription;
        TextView eventRating;
        ImageButton bookmarkButton;
        AppCompatButton reviewsButton;
        RecyclerView reviewRecycler;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventRating = itemView.findViewById(R.id.event_rating);
            eventDescription = itemView.findViewById(R.id.event_description);
            reviewRecycler = itemView.findViewById(R.id.review_latest);
            bookmarkButton = itemView.findViewById(R.id.bookmark_button);
            reviewsButton = itemView.findViewById(R.id.reviews_button);

            if (sPref.getBoolean("isAdmin", false)) {
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(), 0, 0, "Изменить");
            menu.add(getAdapterPosition(), 1, 0, "Удалить");
        }
    }

}
