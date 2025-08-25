package com.example.project.userForSubmoduleIntegrationTask.controller;


import com.example.project.model.User;
import com.example.project.userForSubmoduleIntegrationTask.dto.UserDto;
import com.example.project.userForSubmoduleIntegrationTask.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Integer id) {
        User u = userService.getUserById(id);
        return UserDto.from(u);
    }
}
