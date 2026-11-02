package com.epam.jmp.cloud.service.impl;

import com.epam.jmp.domain.Subscription;
import com.epam.jmp.service.api.ResourceNotFoundException;
import com.epam.jmp.service.api.SubscriptionRepository;
import com.epam.jmp.service.api.SubscriptionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription updateSubscription(Subscription subscription) {
        return subscriptionRepository.findById(subscription.getId())
                .map(existing -> {
                    existing.setUser(subscription.getUser());
                    return subscriptionRepository.save(existing);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Subscription not found with id: " + subscription.getId()));
    }

    @Override
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
}
