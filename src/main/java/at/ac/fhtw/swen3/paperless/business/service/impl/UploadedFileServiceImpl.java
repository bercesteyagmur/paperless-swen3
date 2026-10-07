package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;
import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

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

    @Override
    public UploadedFileModel uploadFile(UploadedFileModel uploadedFileModel) {
        uploadedFileModel.setUploadedAt(LocalDateTime.now());

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);
        return uploadedFileModelMapper.toModel(savedEntity);
    }

    @Override
    public List<UploadedFileModel> getAllFiles() {
        return uploadedFileRepository.findAll().stream()
                .map(uploadedFileModelMapper::toModel)
                .toList();
    }

    @Override
    public UploadedFileModel getFileById(Long id) {
        return findByIdOrThrow(id);
    }

    @Override
    public UploadedFileModel updateFile(Long id, String newFileName) {
        if (newFileName == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "newFileName must not be null");
        }

        UploadedFileModel uploadedFileModel = findByIdOrThrow(id);
        uploadedFileModel.setOriginalFileName(newFileName);

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);
        return uploadedFileModelMapper.toModel(savedEntity);
    }

    @Override
    public void deleteFile(Long id) {
        UploadedFileModel uploadedFileModel = findByIdOrThrow(id);
        uploadedFileRepository.delete(uploadedFileModelMapper.toEntity(uploadedFileModel));
    }
}
