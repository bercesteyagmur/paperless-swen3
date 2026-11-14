package at.ac.fhtw.swen3.paperless.mapper;

import at.ac.fhtw.swen3.paperless.business.model.DocumentSearchResult;
import at.ac.fhtw.swen3.paperless.dto.DocumentSearchResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentSearchMapper {

    DocumentSearchResponse toResponseDto(DocumentSearchResult result);
}
