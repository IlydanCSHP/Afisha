package com.diplom.afisha.model;

import com.diplom.afisha.enums.EventType;

public class Filter {
    private EventType type;

    public Filter() {

    }

    public Filter(EventType type) {
        this.type = type;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }
}
