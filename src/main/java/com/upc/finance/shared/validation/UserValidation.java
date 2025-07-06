package com.upc.finance.shared.validation;

import com.upc.finance.model.dto.UserRequestDto;
import com.upc.finance.shared.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class UserValidation {
    public void validateUser(UserRequestDto userRequestDto) {
        // validation
        // not null
        if (userRequestDto.getUsername() == null || userRequestDto.getUsername().isEmpty())
            throw new ValidationException("Username cannot be null or empty");

        if (userRequestDto.getEmail() == null || userRequestDto.getEmail().isEmpty())
            throw new ValidationException("Email cannot be null or empty");

        if (userRequestDto.getPassword() == null || userRequestDto.getPassword().isEmpty())
            throw new ValidationException("Password cannot be null or empty");

        // length
        if (userRequestDto.getUsername().length() > 15)
            throw new ValidationException("Username must be between 1 and 15 characters long");

        if (userRequestDto.getEmail().length() > 25)
            throw new ValidationException("Email must have a maximum of 25 characters");

        if (userRequestDto.getPassword().length() < 6 || userRequestDto.getPassword().length() > 20)
            throw new ValidationException("Password must be between 6 and 20 characters long");
    }
}
