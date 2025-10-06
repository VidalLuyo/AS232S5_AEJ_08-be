package pe.edu.vallegrande.ApiAI.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.vallegrande.ApiAI.model.Instagram;
import pe.edu.vallegrande.ApiAI.repository.InstagramRepository;
import pe.edu.vallegrande.ApiAI.service.InstagramService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

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
    public Mono<Instagram> getInstagramProfile(String username) {
        return fetchFromApi(username)
                .map(jsonNode -> mapToInstagram(jsonNode, username))
                .flatMap(this::saveOrUpdateProfile)
                .doOnError(error -> System.err.println("Error fetching Instagram profile: " + error.getMessage()));
    }

    @Override
    public Flux<Instagram> getAllProfiles() {
        return instagramRepository.findAll()
                .sort((a, b) -> b.getUpdateDate().compareTo(a.getUpdateDate()));
    }

    @Override
    public Flux<Instagram> getRecentProfiles(int limit) {
        return instagramRepository.findAll()
                .sort((a, b) -> b.getUpdateDate().compareTo(a.getUpdateDate()))
                .take(limit);
    }

    @Override
    public Mono<Void> clearAllHistory() {
        return instagramRepository.deleteAll();
    }

    @Override
    public Mono<Void> deleteProfile(Long id) {
        return instagramRepository.deleteById(id);
    }

    private Mono<JsonNode> fetchFromApi(String username) {
        return instagramWebClient.post()
                .uri("/api/instagram/profile")
                .bodyValue(Map.of("username", username))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

    private Instagram mapToInstagram(JsonNode jsonNode, String username) {
        Instagram instagram = new Instagram();
        JsonNode result = jsonNode.get("result");
        
        if (result == null || result.isNull()) {
            instagram.setUsername(username);
            setTimestamps(instagram);
            return instagram;
        }

        // Mapeo básico
        instagram.setUsername(getTextValue(result, "username", username));
        instagram.setFullName(getTextValue(result, "full_name", null));
        instagram.setProfilePicture(getTextValue(result, "profile_pic_url", null));
        instagram.setBio(getTextValue(result, "biography", null));
        
        // Mapeo de contadores
        instagram.setFollowers(getCountValue(result, "edge_followed_by"));
        instagram.setFollowing(getCountValue(result, "edge_follow"));
        instagram.setPosts(getCountValue(result, "edge_owner_to_timeline_media"));
        
        setTimestamps(instagram);
        return instagram;
    }

    private String getTextValue(JsonNode node, String fieldName, String defaultValue) {
        return node.has(fieldName) ? node.get(fieldName).asText() : defaultValue;
    }

    private Integer getCountValue(JsonNode result, String edgeName) {
        if (result.has(edgeName)) {
            JsonNode edge = result.get(edgeName);
            if (edge.has("count")) {
                return edge.get("count").asInt();
            }
        }
        return null;
    }

    private void setTimestamps(Instagram instagram) {
        LocalDateTime now = LocalDateTime.now();
        if (instagram.getId() == null) {
            instagram.setCreationDate(now);
        }
        instagram.setUpdateDate(now);
    }

    private Mono<Instagram> saveOrUpdateProfile(Instagram instagram) {
        return instagramRepository.findByUsername(instagram.getUsername())
                .flatMap(existing -> updateExisting(instagram, existing))
                .switchIfEmpty(instagramRepository.save(instagram));
    }

    private Mono<Instagram> updateExisting(Instagram newData, Instagram existing) {
        newData.setId(existing.getId());
        newData.setCreationDate(existing.getCreationDate());
        return instagramRepository.save(newData);
    }
}