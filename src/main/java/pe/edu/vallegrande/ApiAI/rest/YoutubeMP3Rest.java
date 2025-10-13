package pe.edu.vallegrande.ApiAI.rest;

import io.swagger.v3.oas.annotations.Operation;
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

    // Convertir video de YouTube a MP3 usando API externa
    @GetMapping("/convertir/{videoId}")
    @Operation(summary = "Convertir video de YouTube a MP3", description = "Convierte un video de YouTube a MP3 usando el videoId y lo guarda en la BD")
    public Mono<YoutubeMP3> convertYoutubeToMP3(@PathVariable String videoId) {
        return youtubeMP3Service.fetchDownloadUrl(videoId);
    }

    // Convertir video de YouTube a MP3 usando URL completa
    @PostMapping("/convertir-url")
    @Operation(summary = "Convertir video usando URL completa", description = "Convierte un video de YouTube a MP3 usando la URL completa")
    public Mono<YoutubeMP3> convertYoutubeByUrl(@RequestParam String url) {
        // Extraer videoId de la URL
        String videoId = extractVideoIdFromUrl(url);
        return youtubeMP3Service.fetchDownloadUrl(videoId);
    }

    // Método auxiliar para extraer videoId de URL
    private String extractVideoIdFromUrl(String url) {
        if (url.contains("watch?v=")) {
            return url.split("watch\\?v=")[1].split("&")[0];
        } else if (url.contains("youtu.be/")) {
            return url.split("youtu.be/")[1].split("\\?")[0];
        }
        return url; // Si ya es solo el ID
    }

    // Historial de conversiones
    @GetMapping("/historial")
    @Operation(summary = "Historial de conversiones MP3", description = "Obtiene todas las canciones convertidas de YouTube")
    public Mono<Iterable<YoutubeMP3>> getYoutubeHistory() {
        return youtubeMP3Service.getYoutubeHistory();
    }

    // Eliminar físicamente un video MP3
    @DeleteMapping("/video/{id}/eliminar-fisico")
    @Operation(summary = "Eliminar físicamente video MP3", description = "Elimina físicamente un video MP3 de la base de datos")
    public Mono<Void> deleteVideoPhysically(@PathVariable Long id) {
        return youtubeMP3Service.deleteVideoPhysically(id);
    }
}
