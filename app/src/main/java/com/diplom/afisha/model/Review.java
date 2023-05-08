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
    private Integer reviewRating;

    @ColumnInfo(name = "review_date")
    private String reviewDate;

    @ColumnInfo(name = "event_id")
    private Long eventId;

    @ColumnInfo(name = "user_id")
    private Long userId;

    public Review() {

    }

    public Review(String reviewText, String reviewDate, Long eventId, Long userId, Integer reviewRating) {
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.eventId = eventId;
        this.userId = userId;
        this.reviewRating = reviewRating;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", reviewText='" + reviewText + '\'' +
                ", reviewRating=" + reviewRating +
                ", reviewDate='" + reviewDate + '\'' +
                ", eventId=" + eventId +
                ", userId=" + userId +
                '}';
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

    public Integer getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(Integer reviewRating) {
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

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }
}
