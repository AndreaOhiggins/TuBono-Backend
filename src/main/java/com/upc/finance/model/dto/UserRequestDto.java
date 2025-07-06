package com.upc.finance.model.dto;

import com.upc.finance.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private String username;

    private String email;

    private  String password;

    private Role role;
}
