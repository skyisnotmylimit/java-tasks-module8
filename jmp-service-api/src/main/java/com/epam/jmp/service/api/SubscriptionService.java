package com.epam.jmp.service.api;

import com.epam.jmp.domain.Subscription;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Subscription operations.
 */
public interface SubscriptionService {

    Subscription createSubscription(Subscription subscription);

    Subscription updateSubscription(Subscription subscription);

    void deleteSubscription(Long id);

    Optional<Subscription> getSubscriptionById(Long id);

    List<Subscription> getAllSubscriptions();
}
