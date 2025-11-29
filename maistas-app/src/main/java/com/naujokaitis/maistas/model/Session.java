package com.naujokaitis.maistas.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class Session {
    @Getter(AccessLevel.NONE)
    private static Session instance;

    @Setter
    private User currentUser;

    private Session() {
        this.currentUser = null;
    }

    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void logout() {
        this.currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
