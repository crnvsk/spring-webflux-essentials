package academy.devdojo.webflux.repository;

import academy.devdojo.webflux.domain.DevDojoUser;
import reactor.core.publisher.Mono;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface DevDojoUserRepository extends ReactiveCrudRepository<DevDojoUser, Integer> {
    
    Mono<DevDojoUser> findByUsername(String username);
}
