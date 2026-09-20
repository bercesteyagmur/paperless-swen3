package at.ac.fhtw.swen3.paperless.mapper;

import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.entity.UploadedFile;

import org.springframework.stereotype.Component;

// Transforms UploadedFile entity -> UploadedFileResponse DTO
@Component
public class UploadedFileMapper {

    public UploadedFileResponse toResponse(UploadedFile uploadedFile) {
        return UploadedFileResponse.builder()
                .id(uploadedFile.getId())
                .originalFileName(uploadedFile.getOriginalFileName())
                .fileType(uploadedFile.getFileType())
                .uploadedAt(uploadedFile.getUploadedAt())
                .uploadedByUsername(uploadedFile.getUploadedBy().getUsername())
                .build();
    }
}
