package academy.devdojo.webflux.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import academy.devdojo.webflux.domain.Anime;

public interface AnimeRepository extends ReactiveCrudRepository<Anime, Integer> {

}
