package com.mobiquity.beer.rider.blacklist;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Holds beer names which are forbidden
 */
@Data
@NoArgsConstructor
@Entity
public class BlackList {

    public BlackList(String name) {
        this.name = name;
    }

    @Id
    @NonNull
    private String name;

}