package at.ac.fhtw.swen3.paperless.business.mapper;

import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import org.mapstruct.Mapper;

// Maps between the business model and the database entity
@Mapper(componentModel = "spring")
public interface UploadedFileModelMapper {

    UploadedFile toEntity(UploadedFileModel uploadedFileModel);

    UploadedFileModel toModel(UploadedFile uploadedFile);
}
