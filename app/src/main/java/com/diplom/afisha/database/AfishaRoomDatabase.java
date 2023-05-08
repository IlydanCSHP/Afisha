package com.diplom.afisha.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.diplom.afisha.dao.EventDao;
import com.diplom.afisha.dao.ReviewDao;
import com.diplom.afisha.dao.TicketDao;
import com.diplom.afisha.dao.UserDao;
import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Review;
import com.diplom.afisha.model.Ticket;
import com.diplom.afisha.model.User;

@Database(entities = {User.class, Event.class, Review.class, Ticket.class}, version = 6)
public abstract class AfishaRoomDatabase extends RoomDatabase {
    public abstract EventDao eventDao();

    public abstract UserDao userDao();
    public abstract ReviewDao reviewDao();
    public abstract TicketDao ticketDao();

    public static volatile AfishaRoomDatabase INSTANCE;

    public static AfishaRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AfishaRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AfishaRoomDatabase.class,
                                    "AfishaDB")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
