package com.oc.projet3.rental.service;

import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;

import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Load user by username (or email) from DB and return UserDetails for Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Assuming your UserRepository has a method to find by username OR email
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username/email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), // or user.getName(), but usually username is email
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
