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

    // Crear perfil
    @PostMapping("/profile")
    @Operation(summary = "Crear perfil de Instagram", description = "Crea un perfil de Instagram")
    public Mono<ResponseEntity<Instagram>> createInstagramProfile(@RequestBody Instagram instagram) {
        return instagramService.createInstagramProfile(instagram)
            .map(createdProfile -> ResponseEntity.ok(createdProfile));
    }

    // Listar todos los perfiles
    @GetMapping("/profiles")
    @Operation(summary = "Listar perfiles de Instagram", description = "Obtiene una lista de todos los perfiles de Instagram")
    public Mono<ResponseEntity<Iterable<Instagram>>> getAllInstagramProfiles() {
        return instagramService.getAllInstagramProfiles()
            .map(ResponseEntity::ok);
    }

    // Obtener perfil por ID
    @GetMapping("/profile/{id}")
    @Operation(summary = "Obtener perfil por ID", description = "Obtiene un perfil de Instagram por su ID")
    public Mono<ResponseEntity<Instagram>> getInstagramProfileById(@PathVariable Long id) {
        return instagramService.getInstagramProfileById(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Editar perfil
    @PutMapping("/profile/{id}")
    @Operation(summary = "Editar perfil de Instagram", description = "Edita un perfil de Instagram existente")
    public Mono<ResponseEntity<Instagram>> updateInstagramProfile(
            @PathVariable Long id, @RequestBody Instagram instagram) {
        return instagramService.updateInstagramProfile(id, instagram)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Eliminar perfil lógicamente
    @PutMapping("/profile/delete/{id}")
    @Operation(summary = "Eliminar perfil de Instagram lógicamente", description = "Elimina lógicamente un perfil de Instagram (cambia estado a 'I')")
    public Mono<ResponseEntity<Instagram>> deleteInstagramProfile(@PathVariable Long id) {
        return instagramService.deleteInstagramProfile(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // Reactivar perfil
    @PutMapping("/profile/reactivate/{id}")
    @Operation(summary = "Reactivar perfil de Instagram", description = "Reactiva un perfil de Instagram (cambia estado a 'A')")
    public Mono<ResponseEntity<Instagram>> reactivateInstagramProfile(@PathVariable Long id) {
        return instagramService.reactivateInstagramProfile(id)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
