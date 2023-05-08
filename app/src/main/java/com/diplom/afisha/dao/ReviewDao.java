package com.diplom.afisha.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.diplom.afisha.model.Review;

import java.util.List;

@Dao
public interface ReviewDao {
    @Insert
    void insert(Review review);

    @Insert
    void insertAll(Review... reviews);

    @Delete
    void delete(Review review);

    @Update
    void update(Review review);

    @Query("SELECT * FROM reviews WHERE id == :id LIMIT 1")
    Review findById(Long id);

    @Query("SELECT * FROM reviews WHERE event_id == :eventId")
    List<Review> findByEventId(Long eventId);

    @Query("SELECT * FROM reviews WHERE user_id == :userId")
    List<Review> findByUserId(Long userId);

    @Query("SELECT AVG(review_rating) FROM reviews WHERE event_id == :eventId")
    Double getEventRating(Long eventId);

    @Query("SELECT * FROM reviews GROUP BY id HAVING id = MAX(id)")
    Review getLatestReview();
    @Query("SELECT * FROM reviews")
    LiveData<List<Review>> getAll();
}
