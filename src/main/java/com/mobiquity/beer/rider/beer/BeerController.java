package com.mobiquity.beer.rider.beer;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/beers")
class BeerController {

    private BeerService beerService;

    public BeerController(BeerService service) {
        this.beerService = service;
    }


    @GetMapping
    public List<Beer> allBeers() {
        return beerService.findAll();
    }

    @GetMapping("/good-beers")
    public List<Beer> goodBeers() {
        return beerService.findAll().stream()
                .filter(beerService::isGreat)
                .collect(Collectors.toList());
    }

}