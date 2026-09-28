package at.ac.fhtw.swen3.paperless.mapper;

import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UploadedFileMapper {

    UploadedFileResponse toResponseDto(UploadedFileModel uploadedFileModel);
}
