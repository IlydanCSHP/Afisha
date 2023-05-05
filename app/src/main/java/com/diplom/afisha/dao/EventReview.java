package com.diplom.afisha.dao;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Review;

import java.util.List;

public class EventReview {
    @Embedded
    public Event event;

    @Relation(
            parentColumn = "id",
            entityColumn = "event_id")
    public List<Review> reviews;
}
