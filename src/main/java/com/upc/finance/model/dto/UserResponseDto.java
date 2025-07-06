package com.upc.finance.model.dto;

import com.upc.finance.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private Long id;

    private String username;

    private String email;

    private Role role;
}
