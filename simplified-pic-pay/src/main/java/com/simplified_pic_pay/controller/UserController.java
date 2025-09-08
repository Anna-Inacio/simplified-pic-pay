package com.simplified_pic_pay.controller;

import com.simplified_pic_pay.domain.user.User;
import com.simplified_pic_pay.dtos.UserDTO;
import com.simplified_pic_pay.repository.UserRepository;
import com.simplified_pic_pay.service.user.UserService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "User manipulation endpoints")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository repository;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created user")
    })
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody UserDTO user) {
        var newUser = userService.createUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        var users = repository.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
