package at.ac.fhtw.swen3.paperless.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// This class is managing which attributes we send back to the frontend
// It is not the same as the UploadedFile entity.
// The entity's "uploadedBy" field holds a full User object and that User has a "password" field.
// If we sent the entity to the frontend directly, password would leak into the JSON response!
// That is why here we only pick "uploadedByUsername"
@Getter
@Builder
@AllArgsConstructor

public class UploadedFileResponse {
    private Long id;
    private String originalFileName;
    private String fileType;
    private LocalDateTime uploadedAt;
    private String uploadedByUsername;
}
