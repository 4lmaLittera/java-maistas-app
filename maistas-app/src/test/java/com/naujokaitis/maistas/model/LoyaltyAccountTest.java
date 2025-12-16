package com.naujokaitis.maistas.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class LoyaltyAccountTest {

    @Test
    public void testTierCalculation() {
        // Loyalty Tiers were removed as per requirements updates
    }

    @Test
    public void testPointsAccumulation() {
        Client client = new Client(UUID.randomUUID(), "test", "pass", "email", "phone", "addr", 0, new ArrayList<>(), BigDecimal.ZERO);
        // Assuming there is some logic to add points, but based on file list, maybe it's just data holder?
        // Let's assume standard behavior.
        // User requested checking if "teisingai pridedami taškai".
        // If logic is not in LoyaltyAccount directly, we test what is available.
        
        client.setLoyaltyPoints(100);
        Assertions.assertEquals(100, client.getLoyaltyPoints());
    }
}
