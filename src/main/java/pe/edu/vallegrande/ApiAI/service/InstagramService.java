package pe.edu.vallegrande.ApiAI.service;

import pe.edu.vallegrande.ApiAI.model.Instagram;
import reactor.core.publisher.Mono;

public interface InstagramService {

    // Crear un perfil
    Mono<Instagram> createInstagramProfile(Instagram instagram);

    // Listar todos los perfiles
    Mono<Iterable<Instagram>> getAllInstagramProfiles();

    // Obtener perfil por ID
    Mono<Instagram> getInstagramProfileById(Long id);

    // Obtener perfil por username
    Mono<Instagram> getInstagramProfile(String username);

    // Editar un perfil
    Mono<Instagram> updateInstagramProfile(Long id, Instagram instagram);

    // Eliminar lógicamente un perfil (cambiar estado de 'A' a 'I')
    Mono<Instagram> deleteInstagramProfile(Long id);

    // Reactivar un perfil (cambiar estado de 'I' a 'A')
    Mono<Instagram> reactivateInstagramProfile(Long id);

    // Historial de consultas
    Mono<Iterable<Instagram>> getInstagramHistory();

    // Cambiar username
    Mono<Instagram> changeUsername(Long id, String newUsername);

    // Buscar perfil usando API externa
    Mono<Instagram> fetchInstagramProfile(String username);

    // Eliminar físicamente por ID
    Mono<Void> deleteInstagramPhysically(Long id);
}
