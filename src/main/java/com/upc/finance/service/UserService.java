package com.upc.finance.service;

import com.upc.finance.model.dto.UserRequestDto;
import com.upc.finance.model.dto.UserResponseDto;

public interface UserService {
    public abstract UserResponseDto createUser(UserRequestDto userRequestDto);
    public abstract UserResponseDto getUserById(Long userId);
    public abstract UserResponseDto loginUser(String email, String password);
    public abstract UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto);
    public abstract void deleteUser(Long userId);
}
