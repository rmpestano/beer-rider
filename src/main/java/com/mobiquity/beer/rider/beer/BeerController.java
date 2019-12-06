package com.mobiquity.beer.rider.beer;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
class BeerController {

    private BeerService beerService;

    public BeerController(BeerService service) {
        this.beerService = service;
    }

    @GetMapping("/good-beers")
    @CrossOrigin(origins = "http://localhost:8081")
    public Collection<Beer> goodBeers() {

        return beerService.findAll().stream()
                .filter(beerService::isGreat)
                .collect(Collectors.toList());
    }

}