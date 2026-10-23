package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.exception.InvalidInputException;
import at.ac.fhtw.swen3.paperless.exception.ResourceAlreadyExistsException;
import at.ac.fhtw.swen3.paperless.exception.ResourceNotFoundException;
import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;
import at.ac.fhtw.swen3.paperless.messaging.OcrMessageProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadedFileServiceImplTest {

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private UploadedFileModelMapper uploadedFileModelMapper;

    @Mock
    private OcrMessageProducer ocrMessageProducer;

    @InjectMocks
    private UploadedFileServiceImpl service;

    @Test
    void uploadSavesFileMetadata() {
        UploadedFileModel input = uploadedFileModel(null, "report.pdf");
        UploadedFile entity = file(null, "report.pdf");
        UploadedFile savedEntity = file(1L, "report.pdf");
        UploadedFileModel savedUploadedFileModel = uploadedFileModel(1L, "report.pdf");

        when(uploadedFileModelMapper.toEntity(input)).thenReturn(entity);
        when(uploadedFileRepository.save(entity)).thenReturn(savedEntity);
        when(uploadedFileModelMapper.toModel(savedEntity)).thenReturn(savedUploadedFileModel);

        UploadedFileModel result = service.uploadFile(input);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOriginalFileName()).isEqualTo("report.pdf");
        assertThat(input.getUploadedAt()).isNotNull();
        verify(uploadedFileRepository).save(entity);
        verify(ocrMessageProducer).sendDocument(1L, "report.pdf");
    }

    @Test
    void uploadSendsSavedFileToOcrQueue() {
        UploadedFileModel input = uploadedFileModel(null, "report.pdf");
        UploadedFile entity = file(null, "report.pdf");
        UploadedFile savedEntity = file(1L, "report.pdf");

        when(uploadedFileModelMapper.toEntity(input)).thenReturn(entity);
        when(uploadedFileRepository.save(entity)).thenReturn(savedEntity);

        service.uploadFile(input);

        verify(ocrMessageProducer).sendDocument(1L, "report.pdf");
    }

    @Test
    void uploadReturnsGeneratedId() {
        UploadedFileModel input = uploadedFileModel(null, "report.pdf");
        UploadedFile entity = file(null, "report.pdf");
        UploadedFile savedEntity = file(1L, "report.pdf");
        UploadedFileModel savedModel = uploadedFileModel(1L, "report.pdf");

        when(uploadedFileModelMapper.toEntity(input)).thenReturn(entity);
        when(uploadedFileRepository.save(entity)).thenReturn(savedEntity);
        when(uploadedFileModelMapper.toModel(savedEntity)).thenReturn(savedModel);

        UploadedFileModel result = service.uploadFile(input);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void uploadRejectsExistingFileName() {
        UploadedFileModel input = uploadedFileModel(null, "report.pdf");
        when(uploadedFileRepository.findByOriginalFileNameIgnoreCase("report.pdf")).thenReturn(file(1L, "report.pdf"));

        assertThrows(ResourceAlreadyExistsException.class, () -> service.uploadFile(input));

        verify(uploadedFileRepository, never()).save(any());
        verify(ocrMessageProducer, never()).sendDocument(any(), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void uploadRejectsBlankFileName(String fileName) {
        UploadedFileModel input = uploadedFileModel(null, fileName);

        assertThrows(InvalidInputException.class, () -> service.uploadFile(input));

        verify(uploadedFileRepository, never()).save(any());
        verify(ocrMessageProducer, never()).sendDocument(any(), any());
    }


    @Test
    void uploadRejectsNonPdfFileName() {
        UploadedFileModel input = uploadedFileModel(null, "report.txt");

        assertThrows(InvalidInputException.class, () -> service.uploadFile(input));

        verify(uploadedFileRepository, never()).save(any());
        verify(ocrMessageProducer, never()).sendDocument(any(), any());
    }

    @Test
    //checks if all stored files are mapped + returend
    void listMapsEveryStoredFile() {
        UploadedFile first = file(1L, "first.pdf");
        UploadedFile second = file(2L, "second.pdf");
        UploadedFileModel firstUploadedFileModel = uploadedFileModel(1L, "first.pdf");
        UploadedFileModel secondUploadedFileModel = uploadedFileModel(2L, "second.pdf");

        when(uploadedFileRepository.findAll()).thenReturn(List.of(first, second));
        when(uploadedFileModelMapper.toModel(first)).thenReturn(firstUploadedFileModel);
        when(uploadedFileModelMapper.toModel(second)).thenReturn(secondUploadedFileModel);

        List<UploadedFileModel> result = service.getAllFiles();

        assertThat(result).containsExactly(firstUploadedFileModel, secondUploadedFileModel);
    }

    @Test
    void getByIdReturnsMappedFile() {
        UploadedFile storedFile = file(3L, "report.pdf");
        UploadedFileModel uploadedFileModel = uploadedFileModel(3L, "report.pdf");

        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(uploadedFileModel);

        UploadedFileModel result = service.getFileById(3L);

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getOriginalFileName()).isEqualTo("report.pdf");
    }

    @Test
    void getByIdRejectsUnknownFile() {
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.getFileById(3L)
        );
    }

    @Test
    void updateRenamesAndSavesFile() {
        UploadedFile storedFile = file(3L, "old.pdf");
        UploadedFileModel storedUploadedFileModel = uploadedFileModel(3L, "old.pdf");
        UploadedFile renamedEntity = file(3L, "new.pdf");
        UploadedFileModel renamedUploadedFileModel = uploadedFileModel(3L, "new.pdf");

        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(storedUploadedFileModel);
        when(uploadedFileModelMapper.toEntity(storedUploadedFileModel)).thenReturn(renamedEntity);
        when(uploadedFileRepository.save(renamedEntity)).thenReturn(renamedEntity);
        when(uploadedFileModelMapper.toModel(renamedEntity)).thenReturn(renamedUploadedFileModel);

        UploadedFileModel result = service.updateFile(3L, "new.pdf");

        assertThat(result.getId()).isEqualTo(3L);
        assertThat(result.getOriginalFileName()).isEqualTo("new.pdf");
        assertThat(storedUploadedFileModel.getOriginalFileName()).isEqualTo("new.pdf");
        verify(uploadedFileRepository).save(renamedEntity);
    }

    @Test
    void updateRejectsNullName() {
        assertThrows(InvalidInputException.class, () -> service.updateFile(3L, null));

        verify(uploadedFileRepository, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"report.txt", "image.jpg", "document"})
    void updateRejectsNonPdfExtension(String fileName) {
        assertThrows(InvalidInputException.class, () -> service.updateFile(3L, fileName));

        verify(uploadedFileRepository, never()).save(any());
    }

    @Test
    void updateRejectsEmptyNameWithPdfExtension() {
        assertThrows(InvalidInputException.class, () -> service.updateFile(3L, ".pdf"));

        verify(uploadedFileRepository, never()).save(any());
    }

    @Test
    void updateRejectsExistingFileName() {
        UploadedFile storedFile = file(3L, "old.pdf");
        UploadedFileModel storedUploadedFileModel = uploadedFileModel(3L, "old.pdf");

        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(storedUploadedFileModel);
        when(uploadedFileRepository.findByOriginalFileNameIgnoreCase("report.pdf"))
                .thenReturn(file(4L, "report.pdf"));

        assertThrows(ResourceAlreadyExistsException.class, () -> service.updateFile(3L, "report.pdf"));

        verify(uploadedFileRepository, never()).save(any());
    }

    @Test
    void updateAllowsSameFileWithDifferentCase() {
        UploadedFile storedFile = file(3L, "report.pdf");
        UploadedFileModel storedUploadedFileModel = uploadedFileModel(3L, "report.pdf");
        UploadedFile renamedEntity = file(3L, "Report.pdf");
        UploadedFileModel renamedUploadedFileModel = uploadedFileModel(3L, "Report.pdf");

        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(storedUploadedFileModel);
        when(uploadedFileRepository.findByOriginalFileNameIgnoreCase("Report.pdf")).thenReturn(storedFile);
        when(uploadedFileModelMapper.toEntity(storedUploadedFileModel)).thenReturn(renamedEntity);
        when(uploadedFileRepository.save(renamedEntity)).thenReturn(renamedEntity);
        when(uploadedFileModelMapper.toModel(renamedEntity)).thenReturn(renamedUploadedFileModel);

        UploadedFileModel result = service.updateFile(3L, "Report.pdf");

        assertThat(result.getOriginalFileName()).isEqualTo("Report.pdf");
        verify(uploadedFileRepository).save(renamedEntity);
    }

    @Test
    void updateRejectsUnknownFile() {
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateFile(3L, "new.pdf"));

        verify(uploadedFileRepository, never()).save(any());
    }

    @Test
    void deleteRemovesExistingFile() {
        UploadedFile storedFile = file(3L, "report.pdf");
        UploadedFileModel uploadedFileModel = uploadedFileModel(3L, "report.pdf");
        UploadedFile entityToDelete = file(3L, "report.pdf");

        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(uploadedFileModel);
        when(uploadedFileModelMapper.toEntity(uploadedFileModel)).thenReturn(entityToDelete);

        service.deleteFile(3L);

        verify(uploadedFileRepository).delete(entityToDelete);
    }

    @Test
    void deleteRejectsUnknownFile() {
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.deleteFile(3L));

        verify(uploadedFileRepository, never()).delete(any(UploadedFile.class));
    }

    private static UploadedFile file(Long id, String name) {
        return UploadedFile.builder()
                .id(id)
                .originalFileName(name)
                .fileType("application/pdf")
                .build();
    }

    private static UploadedFileModel uploadedFileModel(Long id, String name) {
        return UploadedFileModel.builder()
                .id(id)
                .originalFileName(name)
                .fileType("application/pdf")
                .build();
    }
}
