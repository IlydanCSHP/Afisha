package com.diplom.afisha.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.afisha.R;
import com.diplom.afisha.dao.UserDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Ticket;
import com.diplom.afisha.model.User;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    Context context;
    List<Ticket> tickets;
    Activity activity;

    private static SharedPreferences sPref;

    public TicketAdapter(Context context, List<Ticket> tickets, Activity activity) {
        this.context = context;
        this.tickets = tickets;
        this.activity = activity;
        sPref = activity.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.ticket_item, parent, false);
        return new TicketViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        getUser(holder, position);
        holder.ticketDate.setText(tickets.get(position).getTicketDate());
        holder.ticketTime.setText(tickets.get(position).getTicketTime());
        holder.ticketNumber.setText("№" + tickets.get(position).getNumber());
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView ticketUser;
        TextView ticketDate;
        TextView ticketTime;
        TextView ticketNumber;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);

            ticketUser = itemView.findViewById(R.id.ticket_user);
            ticketDate = itemView.findViewById(R.id.ticket_date);
            ticketTime = itemView.findViewById(R.id.ticket_time);
            ticketNumber = itemView.findViewById(R.id.ticket_number);

            if (sPref.getBoolean("isAdmin", false)) {
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(), 0, 0, "Удалить");
        }
    }

    private void getUser(@NonNull TicketViewHolder holder, int position) {
        new Thread(() -> {
            UserDao userDao = AfishaRoomDatabase.getInstance(context).userDao();
            User user = userDao.findById(tickets.get(position).getUserId());
            activity.runOnUiThread(() -> {
                holder.ticketUser.setText(user.getUsername());
            });
        }).start();
    }
}
