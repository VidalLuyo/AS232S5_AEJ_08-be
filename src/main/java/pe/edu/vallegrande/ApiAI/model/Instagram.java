package pe.edu.vallegrande.ApiAI.model;

import lombok.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "instagram_profile")
public class Instagram {
    @Id
    private Long id;

    @Column(value = "username")
    private String username;

    @Column(value = "full_name")
    private String fullName;

    @Column(value = "profile_picture")
    private String profilePicture;

    @Column(value = "bio")
    private String bio;

    @Column(value = "followers")
    private Integer followers;

    @Column(value = "following")
    private Integer following;

    @Column(value = "posts")
    private Integer posts;

    @Column(value = "creation_date")
    private LocalDateTime creationDate;

    @Column(value = "update_date")
    private LocalDateTime updateDate;

    // Nueva columna 'status' con valor por defecto 'A'
    @Column(value = "status")
    private String status = "A"; // Establece el valor predeterminado
}
