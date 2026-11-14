package at.ac.fhtw.swen3.paperless.business.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DocumentSearchResult {

    private Long documentId;
    private String fileName;
}
