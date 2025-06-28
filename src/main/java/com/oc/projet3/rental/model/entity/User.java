package com.oc.projet3.rental.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private Date createdAt;
    private Date updatedAt;

    public User(String name, String email, String hashedPassword) {
        this.name = name;
        this.email = email;
        this.password = hashedPassword;
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }
}
