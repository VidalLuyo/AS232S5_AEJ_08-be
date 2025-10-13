package pe.edu.vallegrande.ApiAI.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.ApiAI.model.Instagram;
import pe.edu.vallegrande.ApiAI.service.InstagramService;
import reactor.core.publisher.Mono;

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

    // Buscar perfil usando API externa
    @GetMapping("/buscar/{username}")
    @Operation(summary = "Buscar perfil de Instagram", description = "Busca un perfil de Instagram usando la API externa y lo guarda en la BD")
    public Mono<ResponseEntity<Instagram>> fetchInstagramProfile(@PathVariable String username) {
        return instagramService.fetchInstagramProfile(username)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Historial de consultas
    @GetMapping("/historial")
    @Operation(summary = "Historial de consultas de Instagram", description = "Obtiene todas las consultas realizadas de Instagram")
    public Mono<ResponseEntity<Iterable<Instagram>>> getInstagramHistory() {
        return instagramService.getInstagramHistory()
            .map(ResponseEntity::ok);
    }

    // Cambiar username en historial
    @PutMapping("/profile/{id}/cambiar-username")
    @Operation(summary = "Cambiar username en historial", description = "Cambia el username de una consulta existente para realizar nueva búsqueda")
    public Mono<ResponseEntity<Instagram>> changeUsername(@PathVariable Long id, @RequestParam String newUsername) {
        return instagramService.changeUsername(id, newUsername)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Eliminar físicamente perfil de Instagram
    @DeleteMapping("/profile/{id}/eliminar-fisico")
    @Operation(summary = "Eliminar físicamente perfil de Instagram", description = "Elimina físicamente un perfil de Instagram de la base de datos")
    public Mono<ResponseEntity<Void>> deleteInstagramPhysically(@PathVariable Long id) {
        return instagramService.deleteInstagramPhysically(id)
            .then(Mono.just(ResponseEntity.ok().<Void>build()))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
