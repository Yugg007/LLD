package com.split.wise.dto;

public class UserDto {
    private final Long userId;
    private final String name;

    public UserDto(Long userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
