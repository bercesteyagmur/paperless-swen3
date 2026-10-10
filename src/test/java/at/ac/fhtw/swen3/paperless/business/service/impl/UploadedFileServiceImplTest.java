package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

        assertThat(result).isSameAs(savedUploadedFileModel);
        assertThat(input.getUploadedAt()).isNotNull();
        verify(uploadedFileRepository).save(entity);
    }

    @Test
    void uploadRejectsExistingFileName() {
        UploadedFileModel input = uploadedFileModel(null, "report.pdf");
        when(uploadedFileRepository.existsByOriginalFileNameIgnoreCase("report.pdf")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.uploadFile(input)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(uploadedFileRepository, never()).save(any());
    }

    @Test
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

        assertThat(result).isSameAs(uploadedFileModel);
    }

    @Test
    void getByIdRejectsUnknownFile() {
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.getFileById(3L)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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

        assertThat(result).isSameAs(renamedUploadedFileModel);
        assertThat(storedUploadedFileModel.getOriginalFileName()).isEqualTo("new.pdf");
        verify(uploadedFileRepository).save(renamedEntity);
    }

    @Test
    void updateRejectsNullName() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateFile(3L, null)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(uploadedFileRepository, never()).save(any());
    }

    @Test
    void updateRejectsUnknownFile() {
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateFile(3L, "new.pdf")
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.deleteFile(3L)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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
