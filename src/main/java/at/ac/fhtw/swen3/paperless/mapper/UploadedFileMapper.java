package at.ac.fhtw.swen3.paperless.mapper;

import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import org.mapstruct.Mapper;

// Maps the business model to the response returned by the API
@Mapper(componentModel = "spring")
public interface UploadedFileMapper {

    UploadedFileResponse toResponseDto(UploadedFileModel uploadedFileModel);
}
