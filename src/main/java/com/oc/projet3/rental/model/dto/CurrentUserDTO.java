package com.oc.projet3.rental.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CurrentUserDTO {
    private Long id;
    private String email;
    private String name;
    private Date created_at;
    private Date updated_at;

    public CurrentUserDTO(Long id, String email, String name, Date created_at, Date updated_at) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
