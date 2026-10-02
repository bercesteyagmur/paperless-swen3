package at.ac.fhtw.swen3.paperless.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TagResponse {

    private Long id;
    private String name;
}
