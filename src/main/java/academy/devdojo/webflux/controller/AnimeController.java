package academy.devdojo.webflux.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import academy.devdojo.webflux.domain.Anime;
import academy.devdojo.webflux.service.AnimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/animes")
@Slf4j
@SecurityScheme(name = "Basic Authentication", type = SecuritySchemeType.HTTP, scheme = "basic")
public class AnimeController {
    private final AnimeService animeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "List all animes", security = @SecurityRequirement(name = "Basic Authentication"), tags = {
            "Animes" })
    public Flux<Anime> findAll() {
        return animeService.findAll();
    }

    @GetMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(security = @SecurityRequirement(name = "Basic Authentication"), tags = {
            "Animes" })
    public Mono<Anime> findById(@PathVariable int id) {
        return animeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(security = @SecurityRequirement(name = "Basic Authentication"), tags = {
            "Animes" })
    public Mono<Anime> save(@Valid @RequestBody Anime anime) {
        return animeService.save(anime);
    }

    @PostMapping("batch")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(security = @SecurityRequirement(name = "Basic Authentication"), tags = {
            "Animes" })
    public Flux<Anime> saveBatch(@RequestBody @Valid List<Anime> animes) {
        return animeService.saveAll(animes);
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "Basic Authentication"), tags = {
            "Animes" })
    public Mono<Void> update(@PathVariable int id, @Valid @RequestBody Anime anime) {
        return animeService.update(anime.withId(id));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(security = @SecurityRequirement(name = "Basic Authentication"), tags = {
            "Animes" })
    public Mono<Void> delete(@PathVariable int id) {
        return animeService.delete(id);
    }
}
