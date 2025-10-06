package pe.edu.vallegrande.ApiAI.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.ApiAI.model.Instagram;
import pe.edu.vallegrande.ApiAI.service.InstagramService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/v1/api/instagram")
@CrossOrigin(origins = "*")
@Tag(name = "Instagram API", description = "API para obtener información de perfiles de Instagram")
public class InstagramRest {

    private final InstagramService instagramService;

    @Autowired
    public InstagramRest(InstagramService instagramService) {
        this.instagramService = instagramService;
    }

    @PostMapping("/profile")
    @Operation(
        summary = "Obtener perfil de Instagram",
        description = "Obtiene la información completa de un perfil de Instagram por username"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil obtenido exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Instagram.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Username inválido o vacío"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public Mono<ResponseEntity<Instagram>> getInstagramProfile(
            @Parameter(description = "Objeto JSON con el username", required = true)
            @RequestBody Map<String, String> request) {
        
        String username = request.get("username");
        
        if (username == null || username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return instagramService.getInstagramProfile(username.trim())
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/profile/{username}")
    @Operation(
        summary = "Obtener perfil de Instagram por URL",
        description = "Obtiene la información completa de un perfil de Instagram usando el username en la URL"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil obtenido exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Instagram.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Username inválido"
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor"
        )
    })
    public Mono<ResponseEntity<Instagram>> getInstagramProfileByPath(
            @Parameter(description = "Username de Instagram", required = true, example = "instagram")
            @PathVariable String username) {
        
        if (username.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return instagramService.getInstagramProfile(username.trim())
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }

    @GetMapping("/history")
    @Operation(
        summary = "Obtener historial de búsquedas",
        description = "Obtiene todas las búsquedas de perfiles de Instagram ordenadas por fecha de actualización"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Historial obtenido exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Instagram.class)
            )
        )
    })
    public Flux<Instagram> getHistory() {
        return instagramService.getAllProfiles();
    }

    @GetMapping("/history/recent")
    @Operation(
        summary = "Obtener búsquedas recientes",
        description = "Obtiene las últimas N búsquedas de perfiles de Instagram"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Búsquedas recientes obtenidas exitosamente"
        )
    })
    public Flux<Instagram> getRecentHistory(
            @Parameter(description = "Número de registros a obtener", example = "50")
            @RequestParam(defaultValue = "50") int limit) {
        return instagramService.getRecentProfiles(limit);
    }

    @DeleteMapping("/history")
    @Operation(
        summary = "Limpiar historial completo",
        description = "Elimina todos los registros del historial de búsquedas"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Historial eliminado exitosamente"
        )
    })
    public Mono<ResponseEntity<Void>> clearHistory() {
        return instagramService.clearAllHistory()
                .then(Mono.just(ResponseEntity.ok().<Void>build()));
    }

    @DeleteMapping("/history/{id}")
    @Operation(
        summary = "Eliminar un registro del historial",
        description = "Elimina un registro específico del historial por ID"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registro eliminado exitosamente"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Registro no encontrado"
        )
    })
    public Mono<ResponseEntity<Void>> deleteHistoryItem(
            @Parameter(description = "ID del registro a eliminar")
            @PathVariable Long id) {
        return instagramService.deleteProfile(id)
                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                .onErrorReturn(ResponseEntity.notFound().build());
    }
}