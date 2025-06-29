package com.oc.projet3.rental.service;

import com.oc.projet3.rental.model.dto.CurrentUserDTO;
import com.oc.projet3.rental.model.dto.LightUserDTO;
import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public Optional<User> getUser(final long id){
        return userRepository.findById(id);
    }

    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(final Long id) {
        userRepository.deleteById(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public Optional<User> getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt)) {
            return Optional.empty();
        }

        String userEmail = ((Jwt) authentication.getPrincipal()).getSubject();

        return userRepository.findByEmail(userEmail);
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user to retrieve.
     * @return An Optional containing the User entity if found, otherwise empty.
     */
    public Optional<LightUserDTO> getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);

        LightUserDTO lightUserDTO = null;
        if (user.isPresent()) {
            lightUserDTO = new LightUserDTO(user.get().getName());
        }

        return Optional.ofNullable(lightUserDTO);
    }

    public Optional<CurrentUserDTO> getCurrentUserDetails(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        User foundUser = user.get();
        CurrentUserDTO currentUserDTO = new CurrentUserDTO(
                foundUser.getId(),
                foundUser.getEmail(),
                foundUser.getName(),
                foundUser.getCreatedAt(),
                foundUser.getUpdatedAt()
        );

        return Optional.of(currentUserDTO);
    }
}
