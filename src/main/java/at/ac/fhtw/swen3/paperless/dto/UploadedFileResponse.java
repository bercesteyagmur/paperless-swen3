package at.ac.fhtw.swen3.paperless.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UploadedFileResponse {
    private Long id;
    private String originalFileName;
    private String fileType;
    private LocalDateTime uploadedAt;
}
