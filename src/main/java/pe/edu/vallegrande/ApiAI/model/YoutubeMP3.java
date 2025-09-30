package pe.edu.vallegrande.ApiAI.model;

import lombok.*;
import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "youtube_video")
public class YoutubeMP3 {
    @Id
    private Long id;

    @Column(value = "video_id")
    private String videoId;

    @Column(value = "video_url")
    private String videoUrl;

    @Column(value = "download_url")
    private String downloadUrl;

    @Column(value = "creation_date")
    private LocalDateTime creationDate;

    @Column(value = "update_date")
    private LocalDateTime updateDate;

    @Column(value = "status")
    private String status = "A"; 
}
