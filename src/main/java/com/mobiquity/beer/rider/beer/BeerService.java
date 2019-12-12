package com.mobiquity.beer.rider.beer;

import com.mobiquity.beer.rider.blacklist.BlackListRepository;
import lombok.NonNull;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeerService {

    BeerRepository beerRepository;

    BlackListRepository blackListRepository;

    public BeerService(BeerRepository beerRepository, BlackListRepository blackListRepository) {
        this.beerRepository = beerRepository;
        this.blackListRepository = blackListRepository;
    }

    public Optional<Beer> save(@NonNull Beer beer) {
        if (notExists(beer)) {
            return Optional.of(beerRepository.save(beer));
        } else {
            return Optional.empty();
        }
    }

    private boolean notExists(Beer beer) {
        return beer != null && beerRepository.countByNameLike(beer.getName()) == 0;
    }

    public List<Beer> findAll() {
        return beerRepository.findAll();
    }

    public List<Beer> findByType(@NonNull BeerType beerType) {
        Beer beerExample = new Beer();
        beerExample.setType(beerType);
        return beerRepository.findAll(Example.of(beerExample));
    }

    public long countAll() {
        return beerRepository.count();
    }

    public void deleteAll() {
        beerRepository.deleteAll();
    }

    /**
     * @param beer beer to check if it is great or not
     * @return <code>true</code> if beer name is not present in blacklist table, <code>false</code> otherwise.
     */
    public boolean isGreat(@NonNull Beer beer) {
        return !blackListRepository.existsById(beer.getName());
    }
}
