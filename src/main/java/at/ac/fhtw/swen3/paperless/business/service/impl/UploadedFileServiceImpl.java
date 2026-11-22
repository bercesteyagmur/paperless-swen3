package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.exception.InvalidInputException;
import at.ac.fhtw.swen3.paperless.exception.ResourceAlreadyExistsException;
import at.ac.fhtw.swen3.paperless.exception.ResourceNotFoundException;
import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.business.validation.FileNameValidator;
import at.ac.fhtw.swen3.paperless.dal.elasticsearch.IndexedDocument;
import at.ac.fhtw.swen3.paperless.dal.elasticsearch.IndexedDocumentRepository;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;
import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;
import at.ac.fhtw.swen3.paperless.messaging.OcrDocumentProducer;
import at.ac.fhtw.swen3.paperless.storage.MinioService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional

public class UploadedFileServiceImpl implements UploadedFileService {

    private static final Logger logger = LoggerFactory.getLogger(UploadedFileServiceImpl.class);

    private final UploadedFileRepository uploadedFileRepository;
    private final UploadedFileModelMapper uploadedFileModelMapper;
    private final OcrDocumentProducer ocrDocumentProducer;
    private final MinioService minioService;
    private final IndexedDocumentRepository indexedDocumentRepository;

    private UploadedFileModel findByIdOrThrow(Long id) {
        return uploadedFileRepository.findById(id)
                .map(uploadedFileModelMapper::toModel)
                .orElseThrow(() -> new ResourceNotFoundException("file not found"));
    }

    @Override
    public UploadedFileModel uploadFile(UploadedFileModel uploadedFileModel, MultipartFile file) {
        String fileName = FileNameValidator.getValidFileName(uploadedFileModel.getOriginalFileName());

        if (uploadedFileRepository.findByOriginalFileNameIgnoreCase(fileName) != null) {
            throw new ResourceAlreadyExistsException("A file with this name already exists");
        }

        uploadedFileModel.setOriginalFileName(fileName);
        uploadedFileModel.setUploadedAt(LocalDateTime.now());

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);
        logger.info("Stored uploaded file {} with id {}", savedEntity.getOriginalFileName(), savedEntity.getId());
        minioService.uploadFile(savedEntity.getId().toString(), file);
        ocrDocumentProducer.sendDocument(savedEntity.getId(), savedEntity.getOriginalFileName());
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

        UploadedFile existingFile = uploadedFileRepository.findByOriginalFileNameIgnoreCase(fileName);
        if (existingFile != null && !existingFile.getId().equals(id)) {
            throw new ResourceAlreadyExistsException("A file with this name already exists");
        }

        uploadedFileModel.setOriginalFileName(fileName);

        UploadedFile entity = uploadedFileModelMapper.toEntity(uploadedFileModel);
        UploadedFile savedEntity = uploadedFileRepository.save(entity);

        indexedDocumentRepository.findById(id).ifPresent(indexedDocument ->
                indexedDocumentRepository.save(new IndexedDocument(
                        id,savedEntity.getOriginalFileName(),  indexedDocument.getContent()))
        );

        logger.info("Renamed file {} to {}", id, savedEntity.getOriginalFileName());
        return uploadedFileModelMapper.toModel(savedEntity);
    }

    @Override
    public void saveSummary(Long id, String summary) {
        UploadedFile uploadedFile = uploadedFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("file not found"));

        uploadedFile.setSummary(summary);
        uploadedFileRepository.save(uploadedFile);
        logger.info("Stored summary for document {}", id);
    }

    @Override
    public void deleteFile(Long id) {
        UploadedFileModel uploadedFileModel = findByIdOrThrow(id);
        minioService.deleteFile(id.toString());
        indexedDocumentRepository.deleteById(id);
        uploadedFileRepository.delete(uploadedFileModelMapper.toEntity(uploadedFileModel));
        logger.info("Deleted file with id {}", id);
    }

}
