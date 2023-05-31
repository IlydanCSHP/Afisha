package com.diplom.afisha.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.diplom.afisha.enums.EventType;

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

    @ColumnInfo(name = "tickets_number")
    private Integer ticketsNumber;

    @ColumnInfo(name = "event_type")
    private EventType type;

    public Event() {

    }

    public Event(String title, String description, String address, Double price, Integer ticketsNumber, EventType type) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.price = price;
        this.ticketsNumber = ticketsNumber;
        this.type = type;
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

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTicketsNumber() {
        return ticketsNumber;
    }

    public void setTicketsNumber(Integer ticketsNumber) {
        this.ticketsNumber = ticketsNumber;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }
}
