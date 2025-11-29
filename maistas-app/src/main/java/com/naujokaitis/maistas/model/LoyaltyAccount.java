package com.naujokaitis.maistas.model;

import lombok.Getter;

import java.util.Objects;

@Getter
public class LoyaltyAccount {

    private final Client client;
    private int pointsBalance;
    private LoyaltyTier tier;

    public LoyaltyAccount(Client client, int initialPoints, LoyaltyTier tier) {
        this.client = Objects.requireNonNull(client, "client must not be null");
        this.pointsBalance = Math.max(0, initialPoints);
        this.tier = Objects.requireNonNullElse(tier, LoyaltyTier.BRONZE);
    }

    public void earnPoints(Order order, int points) {
        Objects.requireNonNull(order, "order must not be null");
        if (points < 0) {
            throw new IllegalArgumentException("points must be positive");
        }
        pointsBalance += points;
        recalculateTier();
    }

    public void redeemPoints(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount must be positive");
        }
        if (amount > pointsBalance) {
            throw new IllegalArgumentException("cannot redeem more points than available");
        }
        pointsBalance -= amount;
        recalculateTier();
    }

    private void recalculateTier() {
        if (pointsBalance >= 5000) {
            tier = LoyaltyTier.PLATINUM;
        } else if (pointsBalance >= 2500) {
            tier = LoyaltyTier.GOLD;
        } else if (pointsBalance >= 1000) {
            tier = LoyaltyTier.SILVER;
        } else {
            tier = LoyaltyTier.BRONZE;
        }
    }
}

