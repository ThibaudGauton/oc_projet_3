package com.oc.projet3.rental.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RentalUpdateRequest {
    private String name;
    private float surface;
    private float price;
    private String description;
}