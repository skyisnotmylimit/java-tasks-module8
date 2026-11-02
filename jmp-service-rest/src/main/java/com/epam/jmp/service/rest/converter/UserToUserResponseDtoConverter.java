package com.epam.jmp.service.rest.converter;

import com.epam.jmp.domain.User;
import com.epam.jmp.dto.UserResponseDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class UserToUserResponseDtoConverter implements Converter<User, UserResponseDto> {

    @Override
    public UserResponseDto convert(User source) {
        return new UserResponseDto(
                source.getId(),
                source.getName(),
                source.getSurname(),
                source.getBirthday() != null ? source.getBirthday().toString() : null
        );
    }
}
