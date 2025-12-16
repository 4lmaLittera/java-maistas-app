package com.naujokaitis.maistas.api.repository;

import com.naujokaitis.maistas.api.model.PricingRule;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PricingRuleRepository extends CrudRepository<PricingRule, UUID> {
}
