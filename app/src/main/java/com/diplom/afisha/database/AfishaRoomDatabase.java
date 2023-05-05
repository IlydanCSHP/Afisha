package com.diplom.afisha.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.diplom.afisha.dao.EventDao;
import com.diplom.afisha.dao.UserDao;
import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Review;
import com.diplom.afisha.model.User;

@Database(entities = {User.class, Event.class, Review.class}, version = 2)
public abstract class AfishaRoomDatabase extends RoomDatabase {
    public abstract EventDao eventDao();

    public abstract UserDao userDao();

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
