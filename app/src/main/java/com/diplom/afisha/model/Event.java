package com.diplom.afisha.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "events")
public class Event {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "event_title")
    private String title;

    @ColumnInfo(name = "event_description")
    private String description;

    @ColumnInfo(name = "event_address")
    private String address;

    @ColumnInfo(name = "event_price")
    private Double price;
    @ColumnInfo(name = "event_rating")
    private Double rating;

    public Event() {

    }
    public Event(String title, String description, String address, Double price) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.price = price;
    }
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
