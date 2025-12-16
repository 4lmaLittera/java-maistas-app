package com.naujokaitis.maistas.api.bootstrap;

import com.naujokaitis.maistas.api.model.DemandLevel;
import com.naujokaitis.maistas.api.model.PricingRule;
import com.naujokaitis.maistas.api.model.TimeRange;
import com.naujokaitis.maistas.api.repository.PricingRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PricingRuleRepository pricingRuleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (pricingRuleRepository.count() == 0) {
            // Seed a default "Lunch Rush" rule (10:00 - 14:00) with 1.2x price
            PricingRule lunchRush = PricingRule.create(
                    "Lunch Rush",
                    new TimeRange(LocalTime.of(10, 0), LocalTime.of(14, 0)),
                    DemandLevel.HIGH,
                    1.2
            );
            pricingRuleRepository.save(lunchRush);

            // Seed a "Dinner Rush" rule (17:00 - 21:00) with 1.3x price
            PricingRule dinnerRush = PricingRule.create(
                    "Dinner Rush",
                    new TimeRange(LocalTime.of(17, 0), LocalTime.of(21, 0)),
                    DemandLevel.PEAK,
                    1.3
            );
            pricingRuleRepository.save(dinnerRush);
            
            // Seed an "All Day" rule for testing (current time coverage guarantee if needed, but risky if overlaps. kept simple for now)
            // Ideally, we'd check if no rules cover current time for test purposes, but "Lunch" and "Dinner" are good starts.
        }
    }
}
