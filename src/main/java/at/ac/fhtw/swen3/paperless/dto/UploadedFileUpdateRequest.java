package at.ac.fhtw.swen3.paperless.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UploadedFileUpdateRequest {

    @NotBlank(message = "File name must not be empty")
    @Size(max = 100, message = "File name must not be longer than 100 characters")
    private String newFileName;
}
