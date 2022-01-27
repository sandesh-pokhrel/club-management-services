package com.fitness.authservice.controller;

import com.fitness.authservice.model.User;
import com.fitness.authservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody User user) {
        return this.userService.saveUser(user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<User> getAllUsers(@RequestParam Map<String, String> paramMap) {
        return this.userService.getAllUsers(paramMap);
    }

    @GetMapping("/usernames")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsernames() {
        return this.userService.getAllUsernames();
    }

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserByUsername(@PathVariable String username) {
        return this.userService.getByUsername(username);
    }
}
