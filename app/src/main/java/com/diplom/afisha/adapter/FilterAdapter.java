package com.diplom.afisha.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.afisha.MainActivity;
import com.diplom.afisha.R;
import com.diplom.afisha.model.Filter;

import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder> {

    Context context;
    List<Filter> filters;
    Activity activity;

    public FilterAdapter(Context context, List<Filter> filters, Activity activity) {
        this.context = context;
        this.filters = filters;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.filter_item, parent, false);

        return new FilterViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        holder.filterName.setText(filters.get(position).getType().toString());

        holder.itemView.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) activity;
            mainActivity.filterEvents(filters.get(position).getType());
        });
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    public static class FilterViewHolder extends RecyclerView.ViewHolder {

        TextView filterName;

        public FilterViewHolder(@NonNull View itemView) {
            super(itemView);

            filterName = itemView.findViewById(R.id.filter_name);
        }
    }
}
