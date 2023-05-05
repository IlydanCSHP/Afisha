package com.diplom.afisha.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "event_title")
    private String title;

    @ColumnInfo(name = "event_description")
    private String description;

//    @ColumnInfo(name = "event_image")
//    private Byte[] image;

    @ColumnInfo(name = "event_rating")
    private Double rating;

    public Event() {

    }

    public Event(String title, String description, Double rating) {
        this.title = title;
        this.description = description;
        this.rating = rating;
    }

//    public Event(String title, String description, Byte[] image, Double rating) {
//        this.title = title;
//        this.description = description;
//        this.image = image;
//        this.rating = rating;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

//    public Byte[] getImage() {
//        return image;
//    }
//
//    public void setImage(Byte[] image) {
//        this.image = image;
//    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                '}';
    }
}
