package com.oc.projet3.rental.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RentalDTO {
    private Long id;
    private String name;
    private float surface;
    private float price;
    private String picture;
    private String description;
    private Long owner_id;

    private Date created_at;
    private Date updated_at;

    public RentalDTO(Long id, String name, float surface, float price, String picture, String description, Long owner_id, Date created_at, Date updated_at) {
        this.id = id;
        this.name = name;
        this.surface = surface;
        this.price = price;
        this.picture = picture;
        this.description = description;
        this.owner_id = owner_id;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public RentalDTO() {
    }
}
