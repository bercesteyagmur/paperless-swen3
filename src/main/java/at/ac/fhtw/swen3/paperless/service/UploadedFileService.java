package at.ac.fhtw.swen3.paperless.service;

import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Business layer contract.
// So controller knows only the interface, never the implementation
public interface UploadedFileService {

    UploadedFileResponse uploadFile(MultipartFile file);

    List<UploadedFileResponse> getAllFiles();

    UploadedFileResponse getFileById(Long id);

    UploadedFileResponse updateFile(Long id, String newFileName);

    void deleteFile(Long id);
}
