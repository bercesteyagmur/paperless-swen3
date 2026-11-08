package at.ac.fhtw.swen3.paperless.business.service;

import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadedFileService {

    UploadedFileModel uploadFile(UploadedFileModel uploadedFileModel, MultipartFile file);

    List<UploadedFileModel> getAllFiles();

    UploadedFileModel getFileById(Long id);

    UploadedFileModel updateFile(Long id, String newFileName);

    void deleteFile(Long id);
}
