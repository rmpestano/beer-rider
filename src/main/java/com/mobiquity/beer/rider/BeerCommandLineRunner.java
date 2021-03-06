package com.mobiquity.beer.rider;

import com.mobiquity.beer.rider.beer.Beer;
import com.mobiquity.beer.rider.beer.BeerRepository;
import com.mobiquity.beer.rider.beer.BeerType;
import com.mobiquity.beer.rider.blacklist.BlackList;
import com.mobiquity.beer.rider.blacklist.BlackListRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.mobiquity.beer.rider.beer.BeerType.BLONDE;
import static com.mobiquity.beer.rider.beer.BeerType.PILSNER;

@Component
@Slf4j
class BeerCommandLineRunner implements CommandLineRunner {


    public BeerCommandLineRunner(BeerRepository beerRepository, BlackListRepository blackListRepository) {
        log.info("Starting beer rider command line runner...");
        createBeers().stream()
                .filter(Objects::nonNull)
                .filter(s -> s.contains(":"))
                .map(b -> b.split(":"))
                .map(splited -> new Beer(splited[0], BeerType.valueOf(splited[1])))
                .forEach(beer -> {
                            if (beerRepository.countByNameLike(beer.getName()) == 0) {
                                beerRepository.save(beer);
                            }
                        }
                );

        blackListRepository.saveAll(createBlackList());
    }

    private List<String> createBeers() {
        return List.of("Budweiser:" + PILSNER, "Kaiser:" + PILSNER, "Hertog:" + PILSNER,
                "Leffe:" + BLONDE);
    }

    private List<BlackList> createBlackList() {
        return List.of(new BlackList("Budweiser"), new BlackList("Heineken"), new BlackList("Coors Light"),
                new BlackList("Kaiser"));
    }


    @Override
    public void run(String... args) throws Exception {

    }
}
