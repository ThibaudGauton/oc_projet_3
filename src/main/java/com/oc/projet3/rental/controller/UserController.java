package com.oc.projet3.rental.controller;

import com.oc.projet3.rental.model.entity.User;
import com.oc.projet3.rental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public Iterable<User> getUsers() {
        return userService.getUsers();
    }
}
