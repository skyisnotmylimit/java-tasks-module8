package com.epam.jmp.dto;

import jakarta.validation.constraints.NotNull;

public class SubscriptionRequestDto {

    private Long id;
    @NotNull
    private Long userId;

    public SubscriptionRequestDto() {
    }

    public SubscriptionRequestDto(Long userId) {
        this.userId = userId;
    }

    public SubscriptionRequestDto(Long id, Long userId) {
        this.id = id;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
