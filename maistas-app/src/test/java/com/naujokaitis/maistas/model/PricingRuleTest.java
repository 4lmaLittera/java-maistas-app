package com.naujokaitis.maistas.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public class PricingRuleTest {

    @Test
    public void testApplyRuleIncrease() {
        TimeRange range = new TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 0));
        PricingRule rule = new PricingRule(UUID.randomUUID(), "Lunch Rush", range, DemandLevel.HIGH, 1.2);
        
        MenuItem dummyItem = new MenuItem(); 
        BigDecimal basePrice = new BigDecimal("10.00");
        
        BigDecimal newPrice = rule.apply(dummyItem, basePrice);
        
        // 10.00 * 1.2 = 12.00
        Assertions.assertEquals(0, newPrice.compareTo(new BigDecimal("12.00")));
    }

    @Test
    public void testApplyRuleDecrease() {
        TimeRange range = new TimeRange(LocalTime.of(15, 0), LocalTime.of(17, 0));
        PricingRule rule = new PricingRule(UUID.randomUUID(), "Happy Hour", range, DemandLevel.LOW, 0.8);
        
        MenuItem dummyItem = new MenuItem();
        BigDecimal basePrice = new BigDecimal("10.00");
        
        BigDecimal newPrice = rule.apply(dummyItem, basePrice);
        
        // 10.00 * 0.8 = 8.000 -> BigDecimal might need scale handling in real app but currently apply() just multiplies
        // 10.00 * 0.8 = 8.000
        Assertions.assertEquals(0, newPrice.compareTo(new BigDecimal("8.000")));
    }
}
