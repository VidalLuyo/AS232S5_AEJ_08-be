package pe.edu.vallegrande.ApiAI.service;

import pe.edu.vallegrande.ApiAI.model.YoutubeMP3;
import reactor.core.publisher.Mono;

public interface YoutubeMP3Service {

    // Obtener el enlace de descarga para un video de YouTube
    Mono<YoutubeMP3> fetchDownloadUrl(String videoId);

    // Crear un nuevo video MP3 (agregarlo a la base de datos)
    Mono<YoutubeMP3> createYoutubeMP3(YoutubeMP3 youtubeMP3);

    // Obtener todos los videos MP3
    Mono<Iterable<YoutubeMP3>> getAllVideos();

    // Obtener un video MP3 por su ID
    Mono<YoutubeMP3> getVideoById(Long id);

    // Editar un video MP3
    Mono<YoutubeMP3> updateVideo(Long id, YoutubeMP3 youtubeMP3);

    // Eliminar lógicamente un video (cambiar el estado a 'I')
    Mono<YoutubeMP3> deleteVideo(Long id);

    // Reactivar un video (cambiar el estado a 'A')
    Mono<YoutubeMP3> reactivateVideo(Long id);
}
