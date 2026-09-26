package at.ac.fhtw.swen3.paperless.business.service;

import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;

import java.util.List;

// Business layer contract.
// So controller knows only the interface, never the implementation
public interface UploadedFileService {

    UploadedFileModel uploadFile(UploadedFileModel uploadedFileModel);

    List<UploadedFileModel> getAllFiles();

    UploadedFileModel getFileById(Long id);

    UploadedFileModel updateFile(Long id, String newFileName);

    void deleteFile(Long id);
}
