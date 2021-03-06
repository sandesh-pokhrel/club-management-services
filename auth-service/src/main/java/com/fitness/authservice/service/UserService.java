package com.fitness.authservice.service;

import com.fitness.authservice.model.User;
import com.fitness.authservice.model.UserLevel;
import com.fitness.authservice.repository.RoleRepository;
import com.fitness.authservice.repository.UserLevelRepository;
import com.fitness.authservice.repository.UserRepository;
import com.fitness.sharedapp.common.Constants;
import com.fitness.sharedapp.exception.NotFoundException;
import com.fitness.sharedapp.service.GenericService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService extends GenericService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserLevelRepository userLevelRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User saveUser(User user, String mode, Integer clubId) {
        if (mode.equals("CREATE") && userRepository.existsByUsernameOrEmail(user.getUsername(), user.getEmail()))
            throw new RuntimeException("Username or Email already exists!");
        if (mode.equals("CREATE")) {
            user.setClubId(clubId);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRoles(Collections.singletonList(roleRepository.findByNameContaining("USER")));
        } else
            this.userRepository
                    .findById(user.getUsername()).orElseThrow(() -> new NotFoundException("Username not found"));
        return this.userRepository.save(user);
    }

    public List<String> getAllUsernames(Integer clubId) {
        return this.userRepository.getOnlyUsernames(clubId);
    }

    public Page<User> getAllUsers(Map<String, String> paramMap, Integer clubId) {
        Integer page = getPageNumber(paramMap);
        String orderBy = getOrderBy(paramMap, "username");
        Sort.Direction order = getOrder(paramMap);
        String search = getSearch(paramMap);
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE,
                order, orderBy);
        if (Objects.nonNull(search))
            return this.userRepository.search(search, clubId, pageable);
        return this.userRepository.findAllByClubId(clubId, pageable);
    }

    public User getByUsername(String username) {
        return this.userRepository.findById(username).orElse(null);
    }

    public List<UserLevel> getAllUserLevels() {
        return this.userLevelRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = this.userRepository.findById(username);
        if (!optionalUser.isPresent()) throw new UsernameNotFoundException("Username not found");
        User user = optionalUser.get();
        List<SimpleGrantedAuthority> roles = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), roles);
    }
}
