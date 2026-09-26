package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;
import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

// Business component for uploaded documents
@Service
@RequiredArgsConstructor
public class UploadedFileServiceImpl implements UploadedFileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UploadedFileModelMapper uploadedFileModelMapper;

    private UploadedFileModel findByIdOrThrow(Long id) {
        return uploadedFileRepository.findById(id)
                .map(uploadedFileModelMapper::toModel)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "file with id " + id + " was not found"));
    }

    // CREATE -> validates the business model and saves a new row
    @Override
    public UploadedFileModel uploadFile(UploadedFileModel uploadedFileModel) {
        uploadedFileModel.setUploadedAt(LocalDateTime.now());

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);
        return uploadedFileModelMapper.toModel(savedEntity);
    }

    // READ all files
    @Override
    public List<UploadedFileModel> getAllFiles() {
        return uploadedFileRepository.findAll().stream()
                .map(uploadedFileModelMapper::toModel)
                .toList();
    }

    // READ one file - 404 if file id does not exist
    @Override
    public UploadedFileModel getFileById(Long id) {
        return findByIdOrThrow(id);
    }

    // UPDATE -> renames originalFileName. 400 if null, 404 if id does not exist.
    @Override
    public UploadedFileModel updateFile(Long id, String newFileName) {
        if (newFileName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "originalFileName must not be null");
        }

        UploadedFileModel uploadedFileModel = findByIdOrThrow(id);
        uploadedFileModel.setOriginalFileName(newFileName);

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);
        return uploadedFileModelMapper.toModel(savedEntity);
    }

    // DELETE file - 404 if id does not exist
    @Override
    public void deleteFile(Long id) {
        UploadedFileModel uploadedFileModel = findByIdOrThrow(id);
        uploadedFileRepository.delete(uploadedFileModelMapper.toEntity(uploadedFileModel));
    }
}
