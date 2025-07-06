package com.upc.finance.service.impl;

import com.upc.finance.model.dto.UserRequestDto;
import com.upc.finance.model.dto.UserResponseDto;
import com.upc.finance.model.entity.User;
import com.upc.finance.repository.UserRepository;
import com.upc.finance.service.UserService;
import com.upc.finance.shared.exception.DuplicateResourceException;
import com.upc.finance.shared.exception.InvalidCredentialsException;
import com.upc.finance.shared.exception.ResourceNotFoundException;
import com.upc.finance.shared.validation.UserValidation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserValidation userValidation;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, UserValidation userValidation) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.userValidation = userValidation;
    }


    @Override
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        var existingUser = userRepository.findByEmail(userRequestDto.getEmail());
        if (existingUser.isPresent()) {
            throw new DuplicateResourceException("This user already exists with the same email");
        }

        System.out.println(" lenght username: " + userRequestDto.getUsername().length());

        // validation
        userValidation.validateUser(userRequestDto);

        var newUser = modelMapper.map(userRequestDto, User.class);
        var createdUser = userRepository.save(newUser);

        return modelMapper.map(createdUser, UserResponseDto.class);
    }

    @Override
    public UserResponseDto getUserById(Long userId) {
        var userResponse = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return modelMapper.map(userResponse, UserResponseDto.class);
    }

    @Override
    public UserResponseDto loginUser(String email, String password) {
        var userResponse = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        if (!userResponse.getPassword().equals(password))
            throw new InvalidCredentialsException("Invalid password for user with email: " + email);
        return modelMapper.map(userResponse, UserResponseDto.class);
    }

    @Override
    public UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto) {
        var existingUser = userRepository.findUserById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        // Validate user request
        userValidation.validateUser(userRequestDto);

        // Update user details
        modelMapper.map(userRequestDto, existingUser);
        var savedUser = userRepository.save(existingUser);
        return modelMapper.map(savedUser, UserResponseDto.class);
    }

    @Override
    public void deleteUser(Long userId) {
        if(!userRepository.existsById(userId))
            throw new ResourceNotFoundException("User not found with id: " + userId);

        userRepository.deleteById(userId);
    }
}
