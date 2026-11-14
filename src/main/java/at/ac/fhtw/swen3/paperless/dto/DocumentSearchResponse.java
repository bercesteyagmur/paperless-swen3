package at.ac.fhtw.swen3.paperless.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DocumentSearchResponse {

    private Long documentId;
    private String fileName;
}
