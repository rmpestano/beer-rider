package com.mobiquity.beer.rider;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.junit5.api.DBRider;
import com.mobiquity.beer.rider.beer.BeerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DBRider
@ActiveProfiles("integration-test")
public class BeerServiceRiderIt {

    @Autowired
    BeerService beerService;

    @BeforeEach
    public void before() {
        beerService.findAll();
    }
    @Test
    @DataSet(value = "beers.yml", cleanAfter = true)
    public void shouldListBeers() {
        assertThat(beerService.countAll()).isEqualTo(3);
    }

}


