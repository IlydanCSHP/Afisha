package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.diplom.afisha.adapter.EventAdapter;
import com.diplom.afisha.adapter.FilterAdapter;
import com.diplom.afisha.dao.EventDao;
import com.diplom.afisha.dao.TicketDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Filter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    SharedPreferences sPref;
    LiveData<List<Event>> events;
    FloatingActionButton addEventButton;
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        sPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        getAllEvents();

        setFilters();

        addEventButton = findViewById(R.id.add_event_button);
        if (sPref.getBoolean("isAdmin", false)) {
            addEventButton.setVisibility(View.VISIBLE);
            addEventButton.setOnClickListener(view -> addEventDialog(view));
        }

        profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(view -> openLogin(view));

        setFiltersRecycler();

        setEventsRecycler();
        searchBox = findViewById(R.id.search_field);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                findEvents(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        getAllTickets();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                showUpdateDialog(item.getGroupId());
                break;
            case 1:
                deleteEvent(item.getGroupId());
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void showUpdateDialog(int groupId) {
        View inflater = LayoutInflater.from(this).inflate(R.layout.update_event_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        EditText eventTitle, eventDescription;
        eventTitle = inflater.findViewById(R.id.event_title);
        eventDescription = inflater.findViewById(R.id.event_description);
        getCurrentEvent(groupId, eventTitle, eventDescription);

        AppCompatButton applyButton = inflater.findViewById(R.id.apply_button);
        AppCompatButton cancelButton = inflater.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        applyButton.setOnClickListener(v -> {
            updateEvent(groupId, eventTitle, eventDescription);
        });
    }

    private void getCurrentEvent(int groupId, EditText eventTitle, EditText eventDescription) {
        new Thread(() -> {
            eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
            Event event = eventDao.findById(eventList.get(groupId).getId());
            eventTitle.setText(event.getTitle());
            eventDescription.setText(event.getDescription());
        }).start();
    }

    private void updateEvent(int groupId, EditText eventTitle, EditText eventDescription) {
        new Thread(() -> {
            eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
            Event event = eventDao.findById(eventList.get(groupId).getId());
            event.setTitle(eventTitle.getText().toString());
            event.setDescription(eventDescription.getText().toString());
            eventDao.update(event);
            runOnUiThread(() -> {
                Toast.makeText(this, "Изменено!", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void findEvents(CharSequence s) {
        EventDao eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
        LiveData<List<Event>> foundEvents = eventDao.findByTitle(s.toString());
        foundEvents.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                eventList = events;
                eventAdapter = new EventAdapter(MainActivity.this, eventList, MainActivity.this);
                eventRecycler.setAdapter(eventAdapter);
            }
        });
    }

    private void deleteEvent(int groupId) {
        new Thread(() -> {
            eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
            Event event = eventDao.findById(eventList.get(groupId).getId());
            eventDao.delete(event);
            runOnUiThread(() -> {
                Toast.makeText(this, "Удалено", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void addEventDialog(View view) {
        View inflater = LayoutInflater.from(this).inflate(R.layout.add_event_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(inflater);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        AppCompatButton applyButton = inflater.findViewById(R.id.apply_button);
        AppCompatButton cancelButton = inflater.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        applyButton.setOnClickListener(v -> {
            EditText eventTitle, eventDescription, eventAddress, eventPrice;
            eventTitle = inflater.findViewById(R.id.event_title);
            eventDescription = inflater.findViewById(R.id.event_description);
            eventAddress = inflater.findViewById(R.id.event_address);
            eventPrice = inflater.findViewById(R.id.event_price);
            Event event = new Event(eventTitle.getText().toString(),
                    eventDescription.getText().toString(),
                    eventAddress.getText().toString(),
                    Double.parseDouble(eventPrice.getText().toString()));
            addEvent(event);
            Toast.makeText(this, "Добавлено!", Toast.LENGTH_SHORT).show();
        });
    }

    private void addEvent(Event event) {
        new Thread(() -> {
            eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
            eventDao.insert(event);
        }).start();
    }

    private void openLogin(View view) {
        sPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (sPref.getBoolean("isAdmin", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View inflater = LayoutInflater.from(this).inflate(R.layout.leave_admin_dialog, null);
            builder.setView(inflater);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

            AppCompatButton signOutButton = inflater.findViewById(R.id.sign_out_button);
            AppCompatButton cancelButton = inflater.findViewById(R.id.cancel_button);

            signOutButton.setOnClickListener(v -> {
                SharedPreferences.Editor ed = sPref.edit();
                ed.putBoolean("isAdmin", false);
                ed.apply();
                addEventButton.setVisibility(View.GONE);
                dialog.dismiss();
                Toast.makeText(this, "Режим администратора отключен", Toast.LENGTH_SHORT).show();
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());
        } else if (sPref.getBoolean("isSignedIn", false)) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

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

    private void getAllEvents() {
        eventDao = AfishaRoomDatabase.getInstance(getApplicationContext()).eventDao();
        events = eventDao.getAll();

        events.observe(this, new Observer<List<Event>>() {
            @Override
            public void onChanged(List<Event> events) {
                eventList = events;
                eventAdapter = new EventAdapter(MainActivity.this, eventList, MainActivity.this);
                eventRecycler.setAdapter(eventAdapter);
                Log.d(TAG, "onChanged: " + events);
            }
        });
    }

    private void getAllTickets() {
        new Thread(() -> {
            TicketDao ticketDao = AfishaRoomDatabase.getInstance(this).ticketDao();
            Log.d(TAG, "getAllTickets: " + ticketDao.getAll());
        }).start();
    }
}