package at.ac.fhtw.swen3.paperless.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagRequest {

    @NotBlank(message = "Tag name must not be empty")
    @Size(max = 50, message = "Tag name must not be longer than 50 characters")
    private String name;
}
