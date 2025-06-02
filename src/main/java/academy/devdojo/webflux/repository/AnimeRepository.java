package academy.devdojo.webflux.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import academy.devdojo.webflux.domain.Anime;
import reactor.core.publisher.Mono;

public interface AnimeRepository extends ReactiveCrudRepository<Anime, Integer> {

    Mono<Anime> findById(int id);
}
