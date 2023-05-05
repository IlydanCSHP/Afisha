package com.diplom.afisha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.diplom.afisha.adapter.EventAdapter;
import com.diplom.afisha.adapter.FilterAdapter;
import com.diplom.afisha.dao.EventDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Filter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView filtersRecycler;
    RecyclerView eventRecycler;
    FilterAdapter filterAdapter;
    EventAdapter eventAdapter;
    List<Filter> filters;
    List<Event> eventList = new ArrayList<>();
    EventDao eventDao;
    ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        getAllEvents();

        setFilters();

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> openLogin(view));

        setFiltersRecycler();

        setEventsRecycler();
    }

    private void openLogin(View view) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void setEventsRecycler() {
        eventRecycler = findViewById(R.id.event_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        eventRecycler.setLayoutManager(manager);
    }

    private void setFiltersRecycler() {
        filtersRecycler = findViewById(R.id.filters_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        filtersRecycler.setLayoutManager(manager);
        filterAdapter = new FilterAdapter(this, filters);
        filtersRecycler.setAdapter(filterAdapter);
    }

    private void setFilters() {
        filters = new ArrayList<>();
        filters.add(new Filter(1L, "Кино"));
        filters.add(new Filter(2L, "Театры"));
        filters.add(new Filter(3L, "Кафе"));
        filters.add(new Filter(4L, "Концерты"));
    }

    class InsertAsyncTask extends AsyncTask<Event, Void, Void> {
        @Override
        protected Void doInBackground(Event... events) {
            eventDao = AfishaRoomDatabase.getInstance(getApplicationContext()).eventDao();
            eventDao.insertAll(events);
            Log.d("eventList", String.valueOf(eventDao.getAll()));
            return null;
        }
    }

    private void getAllEvents() {
        eventDao = AfishaRoomDatabase.getInstance(getApplicationContext()).eventDao();
        LiveData<List<Event>> events = eventDao.getAll();

        events.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                eventList = events;
                eventAdapter = new EventAdapter(MainActivity.this, eventList);
                eventRecycler.setAdapter(eventAdapter);
            }
        });
    }
}