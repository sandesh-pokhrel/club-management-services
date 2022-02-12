package com.fitness.authservice.controller;

import com.fitness.authservice.model.User;
import com.fitness.authservice.request.ChangeLogin;
import com.fitness.authservice.service.UserService;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody User user, @RequestParam String mode) {
        return this.userService.saveUser(user, mode);
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

    @PostMapping(value = "/change-password")
    public void changePassword(@RequestBody ChangeLogin login) {
        if (login == null || login.getUsername() == null)
            throw new IllegalArgumentException("Not all required parameters are passed");

        User user = userService.getByUsername(login.getUsername());
        if (user == null)
            throw new NotFoundException("User " + login.getUsername() + " does not exist !");
        else if (!passwordEncoder.matches(login.getOldPassword(), user.getPassword()))
            throw new BadRequestException("Invalid password for user " + user.getUsername());
        else if (!login.getNewPassword().equals(login.getConfirmPassword()))
            throw new BadRequestException("Password confirmation failed!");
        else if (passwordEncoder.matches(login.getNewPassword(), user.getPassword()))
            throw new BadRequestException("Old and new password cannot be same!");

        user.setPassword(passwordEncoder.encode(login.getNewPassword()));
        userService.saveUser(user, "EDIT");
    }
}
