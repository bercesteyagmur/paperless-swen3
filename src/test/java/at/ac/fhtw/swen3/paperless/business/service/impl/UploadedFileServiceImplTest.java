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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadedFileServiceImplTest {

    @Mock private UploadedFileRepository uploadedFileRepository;
    @Mock private UploadedFileModelMapper uploadedFileModelMapper;
    @InjectMocks private UploadedFileServiceImpl service;

    @Test
    void uploadSavesFileMetadata() {
        UploadedFileModel input = uploadedFileModel(null, "report.pdf");
        UploadedFile entity = file(null, "report.pdf");
        UploadedFile savedEntity = file(1L, "report.pdf");
        UploadedFileModel savedUploadedFileModel = uploadedFileModel(1L, "report.pdf");

        // The mapper prepares the database entity and maps the saved result back to the business model
        when(uploadedFileModelMapper.toEntity(input)).thenReturn(entity);
        when(uploadedFileRepository.save(entity)).thenReturn(savedEntity);
        when(uploadedFileModelMapper.toModel(savedEntity)).thenReturn(savedUploadedFileModel);

        assertThat(service.uploadFile(input)).isSameAs(savedUploadedFileModel);
        assertThat(input.getUploadedAt()).isNotNull();
        verify(uploadedFileRepository).save(entity);
    }

    @Test
    void listMapsEveryStoredFile() {
        UploadedFile first = file(1L, "first.pdf");
        UploadedFile second = file(2L, "second.pdf");
        UploadedFileModel firstUploadedFileModel = uploadedFileModel(1L, "first.pdf");
        UploadedFileModel secondUploadedFileModel = uploadedFileModel(2L, "second.pdf");

        // The repository returns entities and the mapper changes them into business models
        when(uploadedFileRepository.findAll()).thenReturn(List.of(first, second));
        when(uploadedFileModelMapper.toModel(first)).thenReturn(firstUploadedFileModel);
        when(uploadedFileModelMapper.toModel(second)).thenReturn(secondUploadedFileModel);

        assertThat(service.getAllFiles()).containsExactly(firstUploadedFileModel, secondUploadedFileModel);
    }

    @Test
    void getByIdReturnsMappedFile() {
        UploadedFile storedFile = file(3L, "report.pdf");
        UploadedFileModel uploadedFileModel = uploadedFileModel(3L, "report.pdf");

        // The stored entity is mapped before it leaves the business layer
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(uploadedFileModel);

        assertThat(service.getFileById(3L)).isSameAs(uploadedFileModel);
    }

    @Test
    void getByIdRejectsUnknownFile() {
        // The repository returns empty because the file does not exist
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFileById(3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verifyNoInteractions(uploadedFileModelMapper);
    }

    @Test
    void updateRenamesAndSavesFile() {
        UploadedFile storedFile = file(3L, "old.pdf");
        UploadedFileModel storedUploadedFileModel = uploadedFileModel(3L, "old.pdf");
        UploadedFile renamedEntity = file(3L, "new.pdf");
        UploadedFileModel renamedUploadedFileModel = uploadedFileModel(3L, "new.pdf");

        // The entity is mapped to the business model, renamed and mapped back before saving
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(storedUploadedFileModel);
        when(uploadedFileModelMapper.toEntity(storedUploadedFileModel)).thenReturn(renamedEntity);
        when(uploadedFileRepository.save(renamedEntity)).thenReturn(renamedEntity);
        when(uploadedFileModelMapper.toModel(renamedEntity)).thenReturn(renamedUploadedFileModel);

        assertThat(service.updateFile(3L, "new.pdf")).isSameAs(renamedUploadedFileModel);
        assertThat(storedUploadedFileModel.getOriginalFileName()).isEqualTo("new.pdf");
        verify(uploadedFileRepository).save(renamedEntity);
    }

    @Test
    void updateRejectsNullNameBeforeReadingDatabase() {
        assertThatThrownBy(() -> service.updateFile(3L, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(uploadedFileRepository, uploadedFileModelMapper);
    }

    @Test
    void updateRejectsUnknownFile() {
        // The file does not exist so save should not be called
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateFile(3L, "new.pdf"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(uploadedFileRepository, never()).save(any());
        verifyNoInteractions(uploadedFileModelMapper);
    }

    @Test
    void deleteRemovesExistingFile() {
        UploadedFile storedFile = file(3L, "report.pdf");
        UploadedFileModel uploadedFileModel = uploadedFileModel(3L, "report.pdf");
        UploadedFile entityToDelete = file(3L, "report.pdf");

        // The business model is mapped back to an entity before deleting it
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileModelMapper.toModel(storedFile)).thenReturn(uploadedFileModel);
        when(uploadedFileModelMapper.toEntity(uploadedFileModel)).thenReturn(entityToDelete);

        service.deleteFile(3L);

        verify(uploadedFileRepository).delete(entityToDelete);
    }

    @Test
    void deleteRejectsUnknownFile() {
        // The file does not exist so delete should not be called
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteFile(3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(uploadedFileRepository, never()).delete(any(UploadedFile.class));
        verifyNoInteractions(uploadedFileModelMapper);
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
