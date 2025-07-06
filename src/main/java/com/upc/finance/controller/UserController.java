package com.upc.finance.controller;

import com.upc.finance.model.dto.UserRequestDto;
import com.upc.finance.model.dto.UserResponseDto;
import com.upc.finance.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User controller")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user")
    @PostMapping("/users/register")
    @Transactional
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserRequestDto userRequestDto) {
        var res = userService.createUser(userRequestDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @Operation(summary = "Get user by id")
    @GetMapping("/user/{id:\\d+}")
    public  ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        System.out.println("Fetching user with ID: " + id);
        var res = userService.getUserById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // login user
    @Operation(summary = "Login user")
    @PostMapping("/users/login")
    public ResponseEntity<UserResponseDto> loginUser(@RequestParam (name = "email") String email, @RequestParam(name = "password") String password) {
        System.out.println("User login attempt: " + email);
        var res = userService.loginUser(email, password);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // update user
    @Operation(summary = "Update user")
    @PutMapping("/user/{id}")
    @Transactional
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable Long id, @RequestBody UserRequestDto userRequestDto) {
        System.out.println("Updating user with ID: " + id);
        var res = userService.updateUser(id, userRequestDto);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // delete user
    @Operation(summary = "Delete user")
    @DeleteMapping("/user/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        System.out.println("Deleting user with ID: " + id);
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
