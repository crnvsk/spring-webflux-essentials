package academy.devdojo.webflux.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import academy.devdojo.webflux.domain.Anime;
import academy.devdojo.webflux.service.AnimeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@RequestMapping("/animes")
@Slf4j
public class AnimeController {
    private final AnimeService animeService;

    @GetMapping
    private Flux<Anime> listAll() {
        return animeService.findAll();
    }

}
