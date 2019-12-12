package com.mobiquity.beer.rider.blacklist;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Holds beer names which are forbidden
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
public class BlackList {

    @Id
    @NonNull
    private String name;

}