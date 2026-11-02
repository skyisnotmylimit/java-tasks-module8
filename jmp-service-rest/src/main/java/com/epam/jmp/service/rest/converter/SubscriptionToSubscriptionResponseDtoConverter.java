package com.epam.jmp.service.rest.converter;

import com.epam.jmp.domain.Subscription;
import com.epam.jmp.dto.SubscriptionResponseDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionToSubscriptionResponseDtoConverter implements Converter<Subscription, SubscriptionResponseDto> {

    @Override
    public SubscriptionResponseDto convert(Subscription source) {
        return new SubscriptionResponseDto(
                source.getId(),
                source.getUser() != null ? source.getUser().getId() : null,
                source.getStartDate() != null ? source.getStartDate().toString() : null
        );
    }
}
