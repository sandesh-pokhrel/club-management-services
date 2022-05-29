package com.fitness.authservice.controller;

import com.fitness.authservice.feign.ClubFeignClient;
import com.fitness.authservice.model.Club;
import com.fitness.authservice.model.Role;
import com.fitness.authservice.model.User;
import com.fitness.authservice.model.UserLevel;
import com.fitness.authservice.request.ChangeLogin;
import com.fitness.authservice.service.UserService;
import com.fitness.sharedapp.exception.BadRequestException;
import com.fitness.sharedapp.exception.NotFoundException;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final ClubFeignClient clubFeignClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User save(@RequestBody User user,
                     @RequestParam String mode,
                     @RequestHeader("Club-Id") Integer clubId) {
        return this.userService.saveUser(user, mode, clubId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<User> getAllUsers(@RequestParam Map<String, String> paramMap,
                                  @RequestHeader("Club-Id") Integer clubId) {
        return this.userService.getAllUsers(paramMap, clubId);
    }

    @GetMapping("/levels")
    @ResponseStatus(HttpStatus.OK)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<UserLevel> getAllUserLevels() {
        return this.userService.getAllUserLevels();
    }

    @GetMapping("/usernames")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getAllUsernames(@RequestHeader("Club-Id") Integer clubId) {
        return this.userService.getAllUsernames(clubId);
    }

    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public Principal getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Principal) authentication.getPrincipal();
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
        userService.saveUser(user, "EDIT", null);
    }

    @GetMapping("/club-validate")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> validateClubForUser(@RequestParam String username, @RequestParam Integer clubId) {
        Role role = null;
        Map<String, Object> resultMap = new HashMap<>();
        User user = this.userService.getByUsername(username);
        try {
            this.clubFeignClient.getClub(clubId);
        } catch (FeignException.NotFound ex) {
            throw new BadRequestException("Club not found!");
        }
        if (Objects.isNull(user)) {
            resultMap.put("result", false);
            return resultMap;
        } else if (user.getRoles().size() > 0) {
            role = user.getRoles().stream().filter(r -> r.getName().equals("ROLE_ADMIN")).findFirst().orElse(null);
            if (Objects.nonNull(role)) {
                resultMap.put("result", true);
            }
        }
        if (Objects.isNull(role)){
            if (Objects.equals(user.getClubId(), clubId)) resultMap.put("result", true);
            else resultMap.put("result", false);
        }
        resultMap.put("club", clubFeignClient.getClub(clubId));
        return resultMap;
    }
}
