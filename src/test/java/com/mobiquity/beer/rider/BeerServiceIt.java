package com.mobiquity.beer.rider;

import com.github.database.rider.junit5.api.DBRider;
import com.mobiquity.beer.rider.beer.BeerService;
import com.mobiquity.beer.rider.beer.Beer;
import com.mobiquity.beer.rider.exception.BeerNotGreatException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.mobiquity.beer.rider.beer.BeerType.BLONDE;
import static com.mobiquity.beer.rider.beer.BeerType.PILSNER;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DBRider
@ActiveProfiles("integration-test")
public class BeerServiceIt {

    @Autowired
    BeerService beerService;

    @BeforeEach
    public void before() {
        beerService.deleteAll();
    }

    @Test
    public void shouldListBeers() {
        assertThat(beerService.countAll()).isEqualTo(0);
        beerService.save(new Beer("Test", BLONDE));
        beerService.save(new Beer("Test2", PILSNER));
        Assertions.assertThat(beerService.findAll()).hasSize(2)
                .extracting("name","type")
                .contains(tuple("Test", BLONDE),
                          tuple("Test2", PILSNER));
    }

    @Test
    public void shouldNotInsertBeerWhichIsNotGreat() {
        assertThat(beerService.countAll()).isEqualTo(0);
        assertThatThrownBy(() -> beerService.save(new Beer("KAISER", PILSNER)))
                .isInstanceOf(BeerNotGreatException.class)
                .hasMessageContaining("Beer KAISER is not great.");

        assertThat(beerService.countAll()).isEqualTo(0);
    }


}


