package pe.edu.vallegrande.ApiAI.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import pe.edu.vallegrande.ApiAI.model.YoutubeMP3;
import pe.edu.vallegrande.ApiAI.service.YoutubeMP3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/v1/api/youtube")
@Tag(name = "YouTube MP3 API", description = "API para convertir videos de YouTube a MP3")
public class YoutubeMP3Rest {

    private final YoutubeMP3Service youtubeMP3Service;

    @Autowired
    public YoutubeMP3Rest(YoutubeMP3Service youtubeMP3Service) {
        this.youtubeMP3Service = youtubeMP3Service;
    }

    // Crear video MP3 a partir de YouTube
    @PostMapping("/video")
    @Operation(summary = "Crear video MP3 desde YouTube", description = "Crea un video MP3 a partir de un video de YouTube")
    public Mono<YoutubeMP3> createYoutubeVideo(@RequestBody YoutubeMP3 youtubeMP3) {
        return youtubeMP3Service.createYoutubeMP3(youtubeMP3);
    }

    // Obtener todos los videos MP3
    @GetMapping("/videos")
    @Operation(summary = "Obtener todos los videos MP3", description = "Obtiene todos los videos MP3 disponibles")
    public Mono<Iterable<YoutubeMP3>> getAllVideos() {
        return youtubeMP3Service.getAllVideos();
    }

    // Obtener un video MP3 por ID
    @GetMapping("/video/{id}")
    @Operation(summary = "Obtener video MP3", description = "Obtiene un video MP3 por su ID")
    public Mono<YoutubeMP3> getVideoById(@PathVariable Long id) {
        return youtubeMP3Service.getVideoById(id);
    }

    // Editar un video MP3
    @PutMapping("/video/{id}")
    @Operation(summary = "Editar video MP3", description = "Edita los detalles de un video MP3")
    public Mono<YoutubeMP3> updateVideo(@PathVariable Long id, @RequestBody YoutubeMP3 youtubeMP3) {
        return youtubeMP3Service.updateVideo(id, youtubeMP3);
    }

    // Eliminar un video MP3 lógicamente
    @PutMapping("/video/{id}/delete")
    @Operation(summary = "Eliminar video MP3 lógicamente", description = "Elimina lógicamente un video MP3 cambiando su estado a 'I'")
    public Mono<YoutubeMP3> deleteVideo(@PathVariable Long id) {
        return youtubeMP3Service.deleteVideo(id);
    }

    // Reactivar un video MP3
    @PutMapping("/video/{id}/reactivate")
    @Operation(summary = "Reactivar video MP3", description = "Reactiva un video MP3 cambiando su estado a 'A'")
    public Mono<YoutubeMP3> reactivateVideo(@PathVariable Long id) {
        return youtubeMP3Service.reactivateVideo(id);
    }
}
