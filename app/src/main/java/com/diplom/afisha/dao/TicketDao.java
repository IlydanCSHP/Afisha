package com.diplom.afisha.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.diplom.afisha.model.Ticket;

import java.util.List;

@Dao
public interface TicketDao {
    @Insert
    void insert(Ticket ticket);

    @Insert
    void insertAll(Ticket... tickets);

    @Delete
    void delete(Ticket ticket);

    @Update
    void update(Ticket ticket);

    @Query("SELECT * FROM tickets")
    List<Ticket> getAll();

    @Query("SELECT * FROM tickets WHERE event_id == :eventId")
    LiveData<List<Ticket>> findByEventId(Long eventId);
    @Query("SELECT * FROM tickets WHERE id == :id LIMIT 1")
    Ticket findById(Long id);

    @Query("SELECT * FROM tickets WHERE event_id == :eventId AND user_id == :userId LIMIT 1")
    Ticket findByUserEvent(Long eventId, Long userId);
}
