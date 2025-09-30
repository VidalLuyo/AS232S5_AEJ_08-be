package pe.edu.vallegrande.ApiAI.service.impl;

import pe.edu.vallegrande.ApiAI.model.Instagram;
import pe.edu.vallegrande.ApiAI.repository.InstagramRepository;
import pe.edu.vallegrande.ApiAI.service.InstagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@Service
public class InstagramServiceImpl implements InstagramService {

    private final InstagramRepository instagramRepository;

    @Autowired
    public InstagramServiceImpl(InstagramRepository instagramRepository) {
        this.instagramRepository = instagramRepository;
    }

    @Override
    public Mono<Instagram> createInstagramProfile(Instagram instagram) {
        instagram.setCreationDate(LocalDateTime.now());  // Fecha de creación
        instagram.setUpdateDate(LocalDateTime.now());  // Fecha de actualización
        return instagramRepository.save(instagram);
    }

    @Override
    public Mono<Iterable<Instagram>> getAllInstagramProfiles() {
        return instagramRepository.findAll() // Devuelve Flux<Instagram>
                .collectList() // Convierte el Flux a una lista, y luego envuelve eso en un Mono
                .map(list -> (Iterable<Instagram>) list);
    }

    @Override
    public Mono<Instagram> getInstagramProfileById(Long id) {
        return instagramRepository.findById(id);
    }

    @Override
    public Mono<Instagram> getInstagramProfile(String username) {
        return instagramRepository.findByUsername(username);
    }

    @Override
public Mono<Instagram> updateInstagramProfile(Long id, Instagram instagram) {
    return instagramRepository.findById(id)
            .flatMap(existingProfile -> {
                // Mantener la fecha de creación intacta
                existingProfile.setFullName(instagram.getFullName());  // Actualiza el nombre
                existingProfile.setProfilePicture(instagram.getProfilePicture());  // Actualiza la foto de perfil
                existingProfile.setBio(instagram.getBio());  // Actualiza la biografía
                existingProfile.setFollowers(instagram.getFollowers());  // Actualiza los seguidores
                existingProfile.setFollowing(instagram.getFollowing());  // Actualiza los seguidos
                existingProfile.setPosts(instagram.getPosts());  // Actualiza los posts
                
                // Actualizar solo la fecha de modificación
                existingProfile.setUpdateDate(LocalDateTime.now());  // Fecha de modificación (no la de creación)
                
                // Guardar el perfil actualizado
                return instagramRepository.save(existingProfile);
            })
            .switchIfEmpty(Mono.empty());  // Si no se encuentra el perfil, no hace nada
}


    @Override
    public Mono<Instagram> deleteInstagramProfile(Long id) {
        return instagramRepository.findById(id)
                .flatMap(existingProfile -> {
                    existingProfile.setStatus("I"); // Cambiar el estado a inactivo
                    return instagramRepository.save(existingProfile);
                });
    }

    @Override
    public Mono<Instagram> reactivateInstagramProfile(Long id) {
        return instagramRepository.findById(id)
                .flatMap(existingProfile -> {
                    existingProfile.setStatus("A"); // Cambiar el estado a activo
                    return instagramRepository.save(existingProfile);
                });
    }
}
