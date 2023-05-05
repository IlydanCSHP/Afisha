package com.diplom.afisha.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "reviews",
        foreignKeys = {
        @ForeignKey(
                entity = Event.class,
                parentColumns = "id",
                childColumns = "event_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE),
        @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onUpdate = ForeignKey.CASCADE,
                onDelete = ForeignKey.CASCADE
        )}
)
public class Review {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "review_text")
    private String reviewText;

    @ColumnInfo(name = "review_rating")
    private Long reviewRating;

    @ColumnInfo(name = "event_id")
    private Long eventId;

    @ColumnInfo(name = "user_id")
    private Long userId;

    public Review() {

    }

    public Review(Long id, String reviewText, Long reviewRating, Long eventId, Long userId) {
        this.id = id;
        this.reviewText = reviewText;
        this.reviewRating = reviewRating;
        this.eventId = eventId;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Long getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Long reviewRating) {
        this.reviewRating = reviewRating;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
