package com.mobiquity.beer.rider;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import com.github.database.rider.junit5.api.DBRider;
import com.mobiquity.beer.rider.beer.Beer;
import com.mobiquity.beer.rider.beer.BeerService;
import com.mobiquity.beer.rider.blacklist.BlackListRepository;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.mobiquity.beer.rider.beer.BeerType.BLONDE;
import static com.mobiquity.beer.rider.beer.BeerType.PILSNER;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("integration-test")
@DBRider
@DBUnit(leakHunter = true)
public class BeerServiceRiderIt {

    @Autowired
    BeerService beerService;

    @Autowired
    BlackListRepository blackListRepository;


    @Test
    @DataSet("beers.yml")
    public void shouldListBeers() throws SQLException {
        assertThat(beerService.findAll()).hasSize(4);
    }

    @Test
    @DataSet(cleanBefore = true)
    @ExpectedDataSet(value = "beers-expected.yml")
    public void shouldInsertBeers() {
        beerService.save(new Beer("Test", BLONDE));
        beerService.save(new Beer("Test2", PILSNER));
    }

    @Test
    @DataSet("beers-by-type.yml")
    public void shouldFindBeersByType() {
        assertThat(beerService.countAll()).isEqualTo(3);
        List<Beer> blondeBeers = beerService.findByType(BLONDE);
        assertThat(blondeBeers).isNotNull().hasSize(1).extracting("name")
                .contains("Leffe");
    }

    @Test
    @DataSet("good-beers.yml")
    public void shouldFindGreatBeers() {
        assertThat(beerService.countAll()).isEqualTo(3);
        assertThat(blackListRepository.count()).isEqualTo(2);
        Beer leffe = new Beer("Leffe", BLONDE);
        Beer heineken = new Beer("Heineklen", PILSNER);
        Beer budweiser = new Beer("Budweiser", PILSNER);
        assertThat(beerService.isGreat(leffe)).isEqualTo(true);
        assertThat(beerService.isGreat(heineken)).isEqualTo(false);
        assertThat(beerService.isGreat(budweiser)).isEqualTo(false);
    }

    @Test
    @DataSet(provider = GreatBeerProvider.class)
    public void shouldFindGreatBeersUsingProvider() {
        assertThat(beerService.countAll()).isEqualTo(3);
        assertThat(blackListRepository.count()).isEqualTo(2);
        Beer leffe = new Beer("Leffe", BLONDE);
        Beer heineken = new Beer("Heineklen", PILSNER);
        Beer budweiser = new Beer("Budweiser", PILSNER);
        assertThat(beerService.isGreat(leffe)).isEqualTo(true);
        assertThat(beerService.isGreat(heineken)).isEqualTo(false);
        assertThat(beerService.isGreat(budweiser)).isEqualTo(false);
    }

    @Test
    @Disabled
    public void shouldLeakConnections() throws SQLException {
        DriverManager.getConnection("jdbc:h2:mem:beer-test;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
        DriverManager.getConnection("jdbc:h2:mem:beer-test;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
        DriverManager.getConnection("jdbc:h2:mem:beer-test;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
    }


    public static class GreatBeerProvider implements DataSetProvider {
        @Override
        public IDataSet provide() throws DataSetException {
            DataSetBuilder builder = new DataSetBuilder();
            return builder
                    .table("BEER")
                    .columns("ID", "NAME", "TYPE")
                        .values(1, "Leffe", BLONDE.name())
                        .values(2, "Heineklen", PILSNER.name())
                        .values(3, "Budweiser", PILSNER.name())
                    .table("BLACK_LIST")
                    .columns("NAME")
                        .values("Budweiser")
                        .values("Heineklen").build();
        }
    }

}
