package com.mobiquity.beer.rider.beer;

import com.mobiquity.beer.rider.blacklist.BlackList;
import com.mobiquity.beer.rider.blacklist.BlackListRepository;
import com.mobiquity.beer.rider.exception.BeerNotGreatException;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BeerService {

    BeerRepository beerRepository;

    BlackListRepository blackListRepository;

    public BeerService(BeerRepository beerRepository, BlackListRepository blackListRepository) {
        this.beerRepository = beerRepository;
        this.blackListRepository = blackListRepository;
    }

    public Beer save(@NonNull Beer beer) {
        if(notExists(beer) && isGreat(beer)) {
            return beerRepository.save(beer);
        } else {
            throw new BeerNotGreatException(String.format("Beer %s is not great.", beer.getName()));
        }
    }

    private boolean notExists(Beer beer) {
        return beer != null && beerRepository.countByNameLike(beer.getName()) == 0;
    }

    public List<Beer> findAll() {
        return beerRepository.findAll();
    }

    public long countAll() {
        return beerRepository.count();
    }

    public void deleteAll() {
        beerRepository.deleteAll();
    }

    /**
     *
     * @param beer beer to check if it is great or not
     *
     * @return <code>true</code> if beer name is not present in blacklist table, <code>false</code> otherwise.
     */
    public boolean isGreat(@NonNull Beer beer) {
        return  blackListRepository.countByNameIgnoreCase(beer.getName()) == 0 ;
    }
}
