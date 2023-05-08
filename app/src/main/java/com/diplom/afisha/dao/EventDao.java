package com.diplom.afisha.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.diplom.afisha.model.Event;

import java.util.List;

@Dao
public interface EventDao {
    @Insert
    void insert(Event event);

    @Insert
    void insertAll(Event... events);

    @Delete
    void delete(Event event);

    @Update
    void update(Event event);

    @Query("SELECT * FROM events WHERE id == :id LIMIT 1")
    Event findById(long id);

    @Query("SELECT * FROM events")
    LiveData<List<Event>> getAll();

    @Query("SELECT * FROM events WHERE event_title LIKE '%' || :title || '%'")
    LiveData<List<Event>> findByTitle(String title);
}
