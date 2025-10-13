package pe.edu.vallegrande.ApiAI.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pe.edu.vallegrande.ApiAI.model.Instagram;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Repository
public interface InstagramRepository extends ReactiveCrudRepository<Instagram, Long> {
    
    Mono<Instagram> findByUsername(String username);
    
    // Historial ordenado por fecha
    Mono<Iterable<Instagram>> findAllByOrderByCreationDateDesc();
}