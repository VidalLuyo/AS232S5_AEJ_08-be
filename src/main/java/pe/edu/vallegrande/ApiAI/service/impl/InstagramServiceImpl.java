package pe.edu.vallegrande.ApiAI.service.impl;

import pe.edu.vallegrande.ApiAI.model.Instagram;
import pe.edu.vallegrande.ApiAI.repository.InstagramRepository;
import pe.edu.vallegrande.ApiAI.service.InstagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class InstagramServiceImpl implements InstagramService {

    private final InstagramRepository instagramRepository;
    private final WebClient instagramWebClient;

    @Autowired
    public InstagramServiceImpl(InstagramRepository instagramRepository,
            @Qualifier("instagramWebClient") WebClient instagramWebClient) {
        this.instagramRepository = instagramRepository;
        this.instagramWebClient = instagramWebClient;
    }

    @Override
    public Mono<Instagram> createInstagramProfile(Instagram instagram) {
        instagram.setCreationDate(LocalDateTime.now()); // Fecha de creación
        instagram.setUpdateDate(LocalDateTime.now()); // Fecha de actualización
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
                    existingProfile.setFullName(instagram.getFullName()); // Actualiza el nombre
                    existingProfile.setProfilePicture(instagram.getProfilePicture()); // Actualiza la foto de perfil
                    existingProfile.setBio(instagram.getBio()); // Actualiza la biografía
                    existingProfile.setFollowers(instagram.getFollowers()); // Actualiza los seguidores
                    existingProfile.setFollowing(instagram.getFollowing()); // Actualiza los seguidos
                    existingProfile.setPosts(instagram.getPosts()); // Actualiza los posts

                    // Actualizar solo la fecha de modificación
                    existingProfile.setUpdateDate(LocalDateTime.now()); // Fecha de modificación (no la de creación)

                    // Guardar el perfil actualizado
                    return instagramRepository.save(existingProfile);
                })
                .switchIfEmpty(Mono.empty()); // Si no se encuentra el perfil, no hace nada
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

    @Override
    public Mono<Iterable<Instagram>> getInstagramHistory() {
        return instagramRepository.findAll()
                .sort((a, b) -> b.getCreationDate().compareTo(a.getCreationDate()))
                .collectList()
                .map(list -> (Iterable<Instagram>) list);
    }

    @Override
    public Mono<Instagram> changeUsername(Long id, String newUsername) {
        return instagramRepository.findById(id)
                .flatMap(existingProfile -> {
                    // Buscar nuevos datos de la API con el nuevo username
                    return fetchInstagramDataFromApi(newUsername)
                            .flatMap(newData -> {
                                // Mantener ID y fecha de creación original
                                newData.setId(existingProfile.getId());
                                newData.setCreationDate(existingProfile.getCreationDate());
                                newData.setUpdateDate(LocalDateTime.now());

                                // Guardar con todos los datos actualizados
                                return instagramRepository.save(newData);
                            });
                });
    }

    // Método auxiliar para obtener datos de la API sin guardar
    private Mono<Instagram> fetchInstagramDataFromApi(String username) {
        log.info("Obteniendo datos de la API para username: " + username);
        return instagramWebClient.post()
                .uri("/api/instagram/profile")
                .bodyValue(java.util.Map.of("username", username))
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    log.info("Respuesta de la API para " + username + ": " + json);

                    Instagram instagram = new Instagram();
                    instagram.setUsername(username);
                    instagram.setStatus("A");

                    // Buscar el objeto "result" en el JSON
                    if (json.contains("\"result\"")) {
                        // Extraer datos del result
                        if (json.contains("\"full_name\"")) {
                            String fullName = extractJsonValue(json, "full_name");
                            instagram.setFullName(fullName);
                        }

                        if (json.contains("\"biography\"")) {
                            String bio = extractJsonValue(json, "biography");
                            instagram.setBio(bio);
                        }

                        if (json.contains("\"profile_pic_url\"")) {
                            String profilePic = extractJsonValue(json, "profile_pic_url");
                            instagram.setProfilePicture(profilePic);
                        }

                        // Extraer contadores de edges
                        if (json.contains("\"edge_followed_by\"")) {
                            String followersStr = extractEdgeCount(json, "edge_followed_by");
                            try {
                                instagram.setFollowers(Integer.parseInt(followersStr));
                            } catch (NumberFormatException e) {
                                instagram.setFollowers(0);
                            }
                        }

                        if (json.contains("\"edge_follow\"")) {
                            String followingStr = extractEdgeCount(json, "edge_follow");
                            try {
                                instagram.setFollowing(Integer.parseInt(followingStr));
                            } catch (NumberFormatException e) {
                                instagram.setFollowing(0);
                            }
                        }

                        if (json.contains("\"edge_owner_to_timeline_media\"")) {
                            String postsStr = extractEdgeCount(json, "edge_owner_to_timeline_media");
                            try {
                                instagram.setPosts(Integer.parseInt(postsStr));
                            } catch (NumberFormatException e) {
                                instagram.setPosts(0);
                            }
                        }
                    }

                    return instagram;
                })
                .doOnError(error -> log.error("Error obteniendo datos de la API: " + error.getMessage()));
    }

    @Override
    public Mono<Instagram> fetchInstagramProfile(String username) {
        log.info("Buscando perfil de Instagram para username: " + username);
        return instagramWebClient.post()
                .uri("/api/instagram/profile")
                .bodyValue(java.util.Map.of("username", username))
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(json -> {
                    try {
                        log.info("Respuesta de la API: " + json);

                        Instagram instagram = new Instagram();
                        instagram.setUsername(username);
                        instagram.setCreationDate(LocalDateTime.now());
                        instagram.setUpdateDate(LocalDateTime.now());
                        instagram.setStatus("A");

                        // Buscar el objeto "result" en el JSON
                        if (json.contains("\"result\"")) {
                            // Extraer datos del result
                            if (json.contains("\"full_name\"")) {
                                String fullName = extractJsonValue(json, "full_name");
                                instagram.setFullName(fullName);
                            }

                            if (json.contains("\"biography\"")) {
                                String bio = extractJsonValue(json, "biography");
                                instagram.setBio(bio);
                            }

                            if (json.contains("\"profile_pic_url\"")) {
                                String profilePic = extractJsonValue(json, "profile_pic_url");
                                instagram.setProfilePicture(profilePic);
                            }

                            // Extraer contadores de edges
                            if (json.contains("\"edge_followed_by\"")) {
                                String followersStr = extractEdgeCount(json, "edge_followed_by");
                                try {
                                    instagram.setFollowers(Integer.parseInt(followersStr));
                                } catch (NumberFormatException e) {
                                    instagram.setFollowers(0);
                                }
                            }

                            if (json.contains("\"edge_follow\"")) {
                                String followingStr = extractEdgeCount(json, "edge_follow");
                                try {
                                    instagram.setFollowing(Integer.parseInt(followingStr));
                                } catch (NumberFormatException e) {
                                    instagram.setFollowing(0);
                                }
                            }

                            if (json.contains("\"edge_owner_to_timeline_media\"")) {
                                String postsStr = extractEdgeCount(json, "edge_owner_to_timeline_media");
                                try {
                                    instagram.setPosts(Integer.parseInt(postsStr));
                                } catch (NumberFormatException e) {
                                    instagram.setPosts(0);
                                }
                            }
                        }

                        return saveOrUpdateProfile(instagram);

                    } catch (Exception e) {
                        log.error("Error procesando respuesta de Instagram API: " + e.getMessage());
                        return Mono.error(e);
                    }
                })
                .doOnSuccess(profile -> log.info("Perfil guardado exitosamente: " + profile.getUsername()))
                .doOnError(error -> log.error("Error al buscar perfil de Instagram: " + error.getMessage()));
    }

    // Método auxiliar para extraer valores del JSON
    private String extractJsonValue(String json, String key) {
        try {
            String pattern = "\"" + key + "\"\\s*:\\s*\"([^\"]+)\"";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }

            // Si no encuentra con comillas, busca sin comillas (para números)
            pattern = "\"" + key + "\"\\s*:\\s*([^,}]+)";
            p = java.util.regex.Pattern.compile(pattern);
            m = p.matcher(json);
            if (m.find()) {
                return m.group(1).trim();
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer el valor para: " + key);
        }
        return "";
    }

    // Método para extraer count de edges
    private String extractEdgeCount(String json, String edgeName) {
        try {
            String pattern = "\"" + edgeName + "\"\\s*:\\s*\\{[^}]*\"count\"\\s*:\\s*(\\d+)";
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(json);
            if (m.find()) {
                return m.group(1);
            }
        } catch (Exception e) {
            log.warn("No se pudo extraer el count para: " + edgeName);
        }
        return "0";
    }

    // Método para guardar o actualizar perfil
    private Mono<Instagram> saveOrUpdateProfile(Instagram instagram) {
        return instagramRepository.findByUsername(instagram.getUsername())
                .flatMap(existing -> {
                    instagram.setId(existing.getId());
                    instagram.setCreationDate(existing.getCreationDate());
                    return instagramRepository.save(instagram);
                })
                .switchIfEmpty(instagramRepository.save(instagram));
    }

    @Override
    public Mono<Void> deleteInstagramPhysically(Long id) {
        log.info("Eliminando físicamente el perfil de Instagram con ID: " + id);
        return instagramRepository.deleteById(id);
    }
}
