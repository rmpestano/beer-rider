package com.mobiquity.beer.rider;

import com.github.database.rider.junit5.api.DBRider;
import com.mobiquity.beer.rider.beer.Beer;
import com.mobiquity.beer.rider.beer.BeerService;
import com.mobiquity.beer.rider.blacklist.BlackList;
import com.mobiquity.beer.rider.blacklist.BlackListRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.mobiquity.beer.rider.beer.BeerType.BLONDE;
import static com.mobiquity.beer.rider.beer.BeerType.PILSNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("integration-test")
public class BeerServiceIt {

    @Autowired
    BeerService beerService;

    @Autowired
    BlackListRepository blackListRepository;

    @BeforeEach
    public void before() {
        beerService.deleteAll();
        blackListRepository.deleteAll();
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
    public void shouldFindBeersByType() {
        assertThat(beerService.countAll()).isEqualTo(0);
        Beer leffe = new Beer("Leffe", BLONDE);
        Beer heineken = new Beer("Heineklen", PILSNER);
        Beer budweiser = new Beer("Budweiser", PILSNER);

        beerService.save(leffe);
        beerService.save(heineken);
        beerService.save(budweiser);

        assertThat(beerService.countAll()).isEqualTo(3);

        List<Beer> blondeBeers = beerService.findByType(BLONDE);
        assertThat(blondeBeers).isNotNull().hasSize(1).extracting("name")
                .contains("Leffe");

    }

    @Test
    public void shouldFindGreatBeers() {
        Beer leffe = new Beer("Leffe", BLONDE);
        Beer heineken = new Beer("Heineklen", PILSNER);
        Beer budweiser = new Beer("Budweiser", PILSNER);

        beerService.save(leffe);
        beerService.save(heineken);
        beerService.save(budweiser);
        assertThat(beerService.countAll()).isEqualTo(3);

        blackListRepository.save(new BlackList(heineken.getName()));
        blackListRepository.save(new BlackList(budweiser.getName()));
        assertThat(blackListRepository.count()).isEqualTo(2);

        assertThat(beerService.isGreat(leffe)).isEqualTo(true);
        assertThat(beerService.isGreat(heineken)).isEqualTo(false);
        assertThat(beerService.isGreat(budweiser)).isEqualTo(false);

    }

}


