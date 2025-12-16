package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class LoyaltyAccount {

    private final Client client;
    private int pointsBalance;

    public LoyaltyAccount(Client client, int initialPoints) {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.pointsBalance = Math.max(0, initialPoints);
    }

    public void earnPoints(Order order, int points) {
        Objects.requireNonNull(order, "order must not be null");
        if (points < 0) {
            throw new IllegalArgumentException("points must be positive");
        }
        pointsBalance += points;
    }

    public void redeemPoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (amount > pointsBalance) {
            throw new IllegalArgumentException("cannot redeem more points than available");
        }
        pointsBalance -= amount;
    }
}

