package pe.edu.vallegrande.ApiAI.service;

import pe.edu.vallegrande.ApiAI.model.Instagram;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface InstagramService {
    
    /**
     * Obtiene el perfil de Instagram y lo guarda/actualiza en la BD
     */
    Mono<Instagram> getInstagramProfile(String username);
    
    /**
     * Obtiene todos los perfiles guardados (historial completo)
     */
    Flux<Instagram> getAllProfiles();
    
    /**
     * Obtiene los perfiles más recientes limitados
     */
    Flux<Instagram> getRecentProfiles(int limit);
    
    /**
     * Elimina todos los registros del historial
     */
    Mono<Void> clearAllHistory();
    
    /**
     * Elimina un perfil específico por ID
     */
    Mono<Void> deleteProfile(Long id);
}