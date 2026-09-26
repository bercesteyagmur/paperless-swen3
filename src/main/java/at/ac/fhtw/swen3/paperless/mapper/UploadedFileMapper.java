package at.ac.fhtw.swen3.paperless.mapper;

import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.entity.UploadedFile;

import org.mapstruct.Mapper;

// we dont use class because we use mapstruct library
// that is why we don't write the body ourselves
// mapstruct fill the body on itself
@Mapper(componentModel = "spring")
public interface UploadedFileMapper {

    // give me an UploadedFile (entity), and I will return an UploadedFileResponse (DTO).
    // Return type (UploadedFileResponse): the output, DTO that the API sends to the frontend.
    UploadedFileResponse toResponseDto(UploadedFile uploadedFile);
}
