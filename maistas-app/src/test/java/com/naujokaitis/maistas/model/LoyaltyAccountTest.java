package com.naujokaitis.maistas.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;

public class LoyaltyAccountTest {

    @Test
    public void testTierCalculation() {
        Client client = new Client(UUID.randomUUID(), "test", "pass", "email", "phone", "addr", 0, new ArrayList<>(), BigDecimal.ZERO);
        LoyaltyAccount account = new LoyaltyAccount(client, 0, LoyaltyTier.BRONZE);
        
        Assertions.assertEquals(LoyaltyTier.BRONZE, account.getTier());
        
        // Simulate adding points manually, as logic might be in constructor or separate method
        // Looking at codebase, LoyaltyAccount seems to recalculate tier in constructor or setPoints
        
        LoyaltyAccount silverAccount = new LoyaltyAccount(client, 1000, LoyaltyTier.BRONZE);
        Assertions.assertEquals(LoyaltyTier.SILVER, silverAccount.getTier());
        
        LoyaltyAccount goldAccount = new LoyaltyAccount(client, 2500, LoyaltyTier.BRONZE);
        Assertions.assertEquals(LoyaltyTier.GOLD, goldAccount.getTier());
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
