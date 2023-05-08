package com.diplom.afisha.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.diplom.afisha.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Update
    void update(User user);

    @Query("SELECT * FROM users WHERE id == :id LIMIT 1")
    User findById(long id);

    @Query("SELECT * FROM users")
    LiveData<List<User>> getAllLive();
}
