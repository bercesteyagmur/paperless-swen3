package at.ac.fhtw.swen3.paperless.dto;

import lombok.Getter;
import lombok.Setter;

// Request body for PATCH /api/files/{id}, e.g. {"originalFileName": "new.pdf"}
@Getter
@Setter
public class UploadedFileUpdateRequest {
    private String originalFileName;
}
