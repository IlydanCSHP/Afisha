package com.diplom.afisha.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.afisha.R;
import com.diplom.afisha.model.Event;

import java.util.List;
import java.util.Locale;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    Context context;
    List<Event> events;

    public EventAdapter() {

    }

    public EventAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
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
        holder.eventRating.setText(String.format(Locale.getDefault(), "%d", events.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {

        TextView eventTitle;
        TextView eventDescription;
        TextView eventRating;
        ImageButton bookmarkButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventTitle = itemView.findViewById(R.id.event_title);
            eventRating = itemView.findViewById(R.id.event_rating);
            eventDescription = itemView.findViewById(R.id.event_description);

            bookmarkButton = itemView.findViewById(R.id.bookmark_button);
            bookmarkButton.setOnClickListener(view -> {
                if (bookmarkButton.getDrawable().getConstantState() == AppCompatResources.getDrawable(itemView.getContext(), R.drawable.bookmark_icon).getConstantState()) {
                    bookmarkButton.setImageDrawable(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.bookmark_filled));
                    Toast.makeText(itemView.getContext(), "Добавлено в закладки!", Toast.LENGTH_SHORT).show();
                } else {
                    bookmarkButton.setImageDrawable(AppCompatResources.getDrawable(itemView.getContext(), R.drawable.bookmark_icon));
                    Toast.makeText(itemView.getContext(), "Удалено из закладок!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
