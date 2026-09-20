package at.ac.fhtw.swen3.paperless.service.impl;

import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.entity.User;
import at.ac.fhtw.swen3.paperless.mapper.UploadedFileMapper;
import at.ac.fhtw.swen3.paperless.repository.UploadedFileRepository;
import at.ac.fhtw.swen3.paperless.repository.UserRepository;
import at.ac.fhtw.swen3.paperless.service.UploadedFileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Business layer talks to the Repository and uses UploadedFileMapper to turn entities into DTOs before returning them.
@Service
@RequiredArgsConstructor
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;
    private final UploadedFileMapper uploadedFileMapper;

    private UploadedFile findByIdOrThrow(Long id) {
        Optional<UploadedFile> fileOptional = uploadedFileRepository.findById(id);
        if (fileOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file with id " + id + " was not found");
        }
        return fileOptional.get();
    }

    private User findUserByIdOrThrow(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user with id " + userId + " was not found");
        }
        return userOptional.get();
    }

    // CREATE -> reads file's name and type and links it to the user, and saves a new row.
    @Override
    public UploadedFileResponse uploadFile(MultipartFile file, Long userId) {
        User uploadedBy = findUserByIdOrThrow(userId);

        UploadedFile uploadedFile = UploadedFile.builder()
                .originalFileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .uploadedAt(LocalDateTime.now())
                .uploadedBy(uploadedBy)
                .build();

        UploadedFile savedFile = uploadedFileRepository.save(uploadedFile);
        return uploadedFileMapper.toResponse(savedFile);
    }

    // READ all files
    @Override
    public List<UploadedFileResponse> getAllFiles() {
        List<UploadedFile> allFiles = uploadedFileRepository.findAll();
        List<UploadedFileResponse> result = new ArrayList<>();

        for (UploadedFile uploadedFile : allFiles) {
            result.add(uploadedFileMapper.toResponse(uploadedFile));
        }

        return result;
    }

    // READ one file - 404 if file id does not exist
    @Override
    public UploadedFileResponse getFileById(Long id) {
        UploadedFile uploadedFile = findByIdOrThrow(id);
        return uploadedFileMapper.toResponse(uploadedFile);
    }

    // UPDATE -> renames originalFileName. 400 if null, 404 if id does not exist.
    @Override
    public UploadedFileResponse updateFile(Long id, String newFileName) {
        if (newFileName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "originalFileName must not be null");
        }

        UploadedFile uploadedFile = findByIdOrThrow(id);
        uploadedFile.setOriginalFileName(newFileName);
        UploadedFile savedFile = uploadedFileRepository.save(uploadedFile);

        return uploadedFileMapper.toResponse(savedFile);
    }

    // DELETE file - 404 if id does not exist
    @Override
    public void deleteFile(Long id) {
        UploadedFile uploadedFile = findByIdOrThrow(id);
        uploadedFileRepository.delete(uploadedFile);
    }
}
