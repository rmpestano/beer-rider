package com.mobiquity.beer.rider.beer;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
public class Beer {

    public Beer(String name, BeerType type) {
        this.name = name;
        this.type = type;
    }

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String name;

    @Enumerated(EnumType.STRING)
    @NonNull
    private BeerType type;
}