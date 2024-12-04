package com.example.fitfeed.models;

import java.util.List;

public class Friend {
    String id;
    String username;

    public Friend() {}

    public Friend(String id, String username) {
        this.username = username;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
