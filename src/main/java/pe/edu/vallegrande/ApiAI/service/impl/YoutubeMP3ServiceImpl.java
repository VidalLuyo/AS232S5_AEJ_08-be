package pe.edu.vallegrande.ApiAI.service.impl;

import pe.edu.vallegrande.ApiAI.model.YoutubeMP3;
import pe.edu.vallegrande.ApiAI.repository.YoutubeMP3Repository;
import pe.edu.vallegrande.ApiAI.service.YoutubeMP3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class YoutubeMP3ServiceImpl implements YoutubeMP3Service {

    private final YoutubeMP3Repository youtubeMP3Repository;
    private final WebClient youtubeWebClient; // WebClient para la API de YouTube

    LocalDateTime now = LocalDateTime.now();

    @Autowired
    public YoutubeMP3ServiceImpl(YoutubeMP3Repository youtubeMP3Repository,
            @Qualifier("youtubeWebClient") WebClient youtubeWebClient) {
        this.youtubeMP3Repository = youtubeMP3Repository;
        this.youtubeWebClient = youtubeWebClient;
    }

    // Obtener la URL de descarga del video de YouTube
    @Override
    public Mono<YoutubeMP3> fetchDownloadUrl(String videoId) {
        log.info("Obteniendo URL de descarga para el video ID = " + videoId);
        return youtubeWebClient.get()
                .uri("/dl?id=" + videoId) // URL para obtener la URL de descarga
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    // Extraemos la URL de descarga del JSON
                    String downloadUrl = json.replaceAll(".*\"link\":\"([^\"]+)\".*", "$1");
                    return downloadUrl;
                })
                .flatMap(downloadUrl -> {
                    YoutubeMP3 youtubeMP3 = new YoutubeMP3();
                    youtubeMP3.setVideoId(videoId);
                    youtubeMP3.setVideoUrl("https://www.youtube.com/watch?v=" + videoId);
                    youtubeMP3.setDownloadUrl(downloadUrl);
                    youtubeMP3.setCreationDate(now);
                    youtubeMP3.setUpdateDate(now);
                    youtubeMP3.setStatus("A"); // Establecer el estado como activo
                    log.info("Guardando el video con URL de descarga: " + youtubeMP3.toString());
                    return youtubeMP3Repository.save(youtubeMP3);
                });
    }

    // Crear un nuevo video (con sus datos de descarga)
    public Mono<YoutubeMP3> createYoutubeMP3(YoutubeMP3 youtubeMP3) {
        youtubeMP3.setCreationDate(LocalDateTime.now());
        youtubeMP3.setUpdateDate(LocalDateTime.now());
        return youtubeMP3Repository.save(youtubeMP3);
    }

    // Obtener todos los videos
    @Override
    public Mono<Iterable<YoutubeMP3>> getAllVideos() {
        return Mono.defer(() -> youtubeMP3Repository.findAll() // Devuelve Flux<YoutubeMP3>
                .collectList() // Convierte el Flux a Mono<List<YoutubeMP3>>
                .map(list -> (Iterable<YoutubeMP3>) list) // Convierte List<YoutubeMP3> a Iterable<YoutubeMP3>
        );
    }

    // Obtener un video por ID
    public Mono<YoutubeMP3> getVideoById(Long id) {
        return youtubeMP3Repository.findById(id);
    }

    // Actualizar un video
    public Mono<YoutubeMP3> updateVideo(Long id, YoutubeMP3 youtubeMP3) {
        return youtubeMP3Repository.findById(id)
                .flatMap(existingVideo -> {
                    existingVideo.setVideoId(youtubeMP3.getVideoId());
                    existingVideo.setVideoUrl(youtubeMP3.getVideoUrl());
                    existingVideo.setDownloadUrl(youtubeMP3.getDownloadUrl());
                    existingVideo.setUpdateDate(LocalDateTime.now());
                    return youtubeMP3Repository.save(existingVideo);
                });
    }

    // Eliminar un video lógicamente (cambiar estado a 'I')
    public Mono<YoutubeMP3> deleteVideo(Long id) {
        return youtubeMP3Repository.findById(id)
                .flatMap(existingVideo -> {
                    existingVideo.setStatus("I"); // Cambiar el estado a inactivo
                    return youtubeMP3Repository.save(existingVideo);
                });
    }

    // Reactivar un video (cambiar estado a 'A')
    public Mono<YoutubeMP3> reactivateVideo(Long id) {
        return youtubeMP3Repository.findById(id)
                .flatMap(existingVideo -> {
                    existingVideo.setStatus("A"); // Cambiar el estado a activo
                    return youtubeMP3Repository.save(existingVideo);
                });
    }
}
