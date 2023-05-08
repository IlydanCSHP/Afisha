package com.diplom.afisha.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "tickets",
        foreignKeys = {
                @ForeignKey(
                        entity = Event.class,
                        parentColumns = "id",
                        childColumns = "event_id",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = User.class,
                        parentColumns = "id",
                        childColumns = "user_id",
                        onUpdate = ForeignKey.CASCADE,
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Ticket {
    @PrimaryKey(autoGenerate = true)
    private Long id;

    @ColumnInfo(name = "event_id")
    private Long eventId;

    @ColumnInfo(name = "user_id")
    private Long userId;

    @ColumnInfo(name = "ticket_date")
    private String ticketDate;

    @ColumnInfo(name = "ticket_time")
    private String ticketTime;

    public Ticket() {

    }

    public Ticket(Long eventId, Long userId, String ticketDate, String ticketTime) {
        this.eventId = eventId;
        this.userId = userId;
        this.ticketDate = ticketDate;
        this.ticketTime = ticketTime;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", eventId=" + eventId +
                ", userId=" + userId +
                ", ticketDate='" + ticketDate + '\'' +
                ", ticketTime='" + ticketTime + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTicketDate() {
        return ticketDate;
    }

    public void setTicketDate(String ticketDate) {
        this.ticketDate = ticketDate;
    }

    public String getTicketTime() {
        return ticketTime;
    }

    public void setTicketTime(String ticketTime) {
        this.ticketTime = ticketTime;
    }
}
