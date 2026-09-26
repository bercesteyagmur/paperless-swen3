package at.ac.fhtw.swen3.paperless.business.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// This is the model used by the business layer
// It has no database annotations because it is not stored directly
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadedFileModel {

    private Long id;
    private String originalFileName;
    private String fileType;
    private LocalDateTime uploadedAt;
}
