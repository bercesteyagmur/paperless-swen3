package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.exception.InvalidInputException;
import at.ac.fhtw.swen3.paperless.exception.ResourceAlreadyExistsException;
import at.ac.fhtw.swen3.paperless.exception.ResourceNotFoundException;
import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.business.validation.FileNameValidator;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;
import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;
import at.ac.fhtw.swen3.paperless.messaging.OcrMessageProducer;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class UploadedFileServiceImpl implements UploadedFileService {

    private static final Logger logger = LoggerFactory.getLogger(UploadedFileServiceImpl.class);

    private final UploadedFileRepository uploadedFileRepository;
    private final UploadedFileModelMapper uploadedFileModelMapper;
    private final OcrMessageProducer ocrMessageProducer;

    private UploadedFileModel findByIdOrThrow(Long id) {
        return uploadedFileRepository.findById(id)
                .map(uploadedFileModelMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("file not found"));
    }

    @Override
    public UploadedFileModel uploadFile(UploadedFileModel uploadedFileModel) {
        String fileName = FileNameValidator.getValidFileName(uploadedFileModel.getOriginalFileName());

        if (uploadedFileRepository.existsByOriginalFileNameIgnoreCase(fileName)) {
            throw new ResourceAlreadyExistsException("A file with this name already exists");
        }

        uploadedFileModel.setOriginalFileName(fileName);
        uploadedFileModel.setUploadedAt(LocalDateTime.now());

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);
        logger.info("Stored uploaded file {} with id {}", savedEntity.getOriginalFileName(), savedEntity.getId());
        ocrMessageProducer.sendDocument(savedEntity.getId(), savedEntity.getOriginalFileName());
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
            throw new InvalidInputException("newFileName must not be null");
        }

        String fileName = FileNameValidator.getValidFileName(newFileName);
        UploadedFileModel uploadedFileModel = findByIdOrThrow(id);

        if (uploadedFileRepository.existsByOriginalFileNameIgnoreCaseAndIdNot(fileName, id)) {
            throw new ResourceAlreadyExistsException("A file with this name already exists");
        }

        uploadedFileModel.setOriginalFileName(fileName);

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);
        logger.info("Renamed file {} to {}", id, savedEntity.getOriginalFileName());
        return uploadedFileModelMapper.toModel(savedEntity);
    }

    @Override
    public void deleteFile(Long id) {
        UploadedFileModel uploadedFileModel = findByIdOrThrow(id);
        uploadedFileRepository.delete(uploadedFileModelMapper.toEntity(uploadedFileModel));
        logger.info("Deleted file with id {}", id);
    }

}
