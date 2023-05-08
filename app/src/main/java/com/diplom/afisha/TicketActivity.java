package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.afisha.adapter.TicketAdapter;
import com.diplom.afisha.dao.TicketDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Ticket;

import java.util.ArrayList;
import java.util.List;

public class TicketActivity extends AppCompatActivity {

    RecyclerView ticketsRecycler;
    TicketAdapter ticketAdapter;
    List<Ticket> ticketList;
    LiveData<List<Ticket>> ticketsLive;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        ticketList = new ArrayList<>();

        intent = getIntent();
        ticketsRecycler = findViewById(R.id.tickets_recycler);
        Log.d(TAG, "onCreate: " + ticketsRecycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        ticketsRecycler.setLayoutManager(manager);
        ticketAdapter = new TicketAdapter(this, ticketList, this);
        ticketsRecycler.setAdapter(ticketAdapter);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> onBackPressed());
        getTickets();

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {
            deleteTicket(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteTicket(int groupId) {
        new Thread(() -> {
            TicketDao ticketDao = AfishaRoomDatabase.getInstance(this).ticketDao();
            Ticket ticket = ticketDao.findById(ticketList.get(groupId).getId());
            ticketDao.delete(ticket);
        }).start();
    }

    private void getTickets() {
        TicketDao ticketDao = AfishaRoomDatabase.getInstance(this).ticketDao();
        ticketsLive = ticketDao.findByEventId(intent.getLongExtra("event_id", 0L));

        ticketsLive.observe(this, tickets -> {
            ticketList = tickets;
            ticketAdapter = new TicketAdapter(TicketActivity.this, ticketList, TicketActivity.this);
            ticketsRecycler.setAdapter(ticketAdapter);
        });
    }
}