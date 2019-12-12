package com.mobiquity.beer.rider;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.DataSetFormat;
import com.github.database.rider.core.api.dataset.DataSetProvider;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.core.api.exporter.BuilderType;
import com.github.database.rider.core.api.exporter.ExportDataSet;
import com.github.database.rider.core.dataset.builder.DataSetBuilder;
import com.github.database.rider.junit5.api.DBRider;
import com.mobiquity.beer.rider.beer.Beer;
import com.mobiquity.beer.rider.beer.BeerService;
import com.mobiquity.beer.rider.blacklist.BlackList;
import com.mobiquity.beer.rider.blacklist.BlackListRepository;
import org.assertj.core.api.Assertions;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.h2.Driver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static com.mobiquity.beer.rider.beer.BeerType.BLONDE;
import static com.mobiquity.beer.rider.beer.BeerType.PILSNER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@SpringBootTest
@ActiveProfiles("integration-test")
@DBRider
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
    @DataSet("beers.yml")
    public void shouldListBeers() throws SQLException {
        //assertThat(beerService.countAll()).isEqualTo(0);
        //beerService.save(new Beer("Test", BLONDE));
        //beerService.save(new Beer("Test2", PILSNER));
        assertThat(beerService.findAll()).hasSize(4);
    }

    @Test
    public void shouldLeakConnections() throws SQLException {
        DriverManager.getConnection("jdbc:h2:mem:beer-test;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
        DriverManager.getConnection("jdbc:h2:mem:beer-test;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
        DriverManager.getConnection("jdbc:h2:mem:beer-test;DB_CLOSE_ON_EXIT=FALSE", "sa", "");
    }

    @Test
    @DataSet(cleanBefore = true)
    @ExpectedDataSet(value = "beers-expected.yml")
    public void shouldInsertBeers() {
        beerService.save(new Beer("Test", BLONDE));
        beerService.save(new Beer("Test2", PILSNER));
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
    @ExportDataSet(outputName = "target/good-beers")
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

    public static class BeerProvider implements DataSetProvider {

        @Override
        public IDataSet provide() throws DataSetException {
            DataSetBuilder builder = new DataSetBuilder();
            return builder
                    .table("BEER")
                        .row()
                    .column("ID", 1)
                    .column("NAME", "Budweiser2")
                    .column("TYPE", "PILSNER")
                        .row()
                    .column("ID", 2)
                    .column("NAME", "hertog2")
                    .column("TYPE", "PILSNER")
                        .row()
                    .column("ID", 3)
                    .column("NAME", "Heineken")
                    .column("TYPE", "PILSNER")
                        .table("BLACK_LIST")
                    .build();
        }
    }

}


