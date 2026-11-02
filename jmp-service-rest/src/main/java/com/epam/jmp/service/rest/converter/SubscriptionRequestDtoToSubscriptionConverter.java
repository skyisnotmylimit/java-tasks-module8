package com.epam.jmp.service.rest.converter;

import com.epam.jmp.domain.Subscription;
import com.epam.jmp.domain.User;
import com.epam.jmp.dto.SubscriptionRequestDto;
import com.epam.jmp.service.api.ResourceNotFoundException;
import com.epam.jmp.service.api.UserService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SubscriptionRequestDtoToSubscriptionConverter implements Converter<SubscriptionRequestDto, Subscription> {

    private final UserService userService;

    public SubscriptionRequestDtoToSubscriptionConverter(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Subscription convert(SubscriptionRequestDto source) {
        Subscription subscription = new Subscription();
        subscription.setId(source.getId());
        User user = userService.getUserById(source.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + source.getUserId()));
        subscription.setUser(user);
        subscription.setStartDate(LocalDate.now());
        return subscription;
    }
}
