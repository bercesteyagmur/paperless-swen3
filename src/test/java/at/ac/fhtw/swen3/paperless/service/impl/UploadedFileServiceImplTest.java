package at.ac.fhtw.swen3.paperless.service.impl;

import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.mapper.UploadedFileMapper;
import at.ac.fhtw.swen3.paperless.repository.UploadedFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UploadedFileServiceImplTest {

    @Mock private UploadedFileRepository uploadedFileRepository;
    @Mock private UploadedFileMapper uploadedFileMapper;
    @Mock private MultipartFile multipartFile;
    @InjectMocks private UploadedFileServiceImpl service;

    @Test
    void uploadSavesFileMetadata() {
        UploadedFileResponse response = response("report.pdf");

        // Tell the mocks what they should return, so we do not need a real database here
        when(multipartFile.getOriginalFilename()).thenReturn("report.pdf");
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(uploadedFileRepository.save(any(UploadedFile.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(uploadedFileMapper.toResponseDto(any(UploadedFile.class))).thenReturn(response);

        assertThat(service.uploadFile(multipartFile)).isSameAs(response);

        ArgumentCaptor<UploadedFile> savedFile = ArgumentCaptor.forClass(UploadedFile.class);
        verify(uploadedFileRepository).save(savedFile.capture());
        assertThat(savedFile.getValue().getOriginalFileName()).isEqualTo("report.pdf");
        assertThat(savedFile.getValue().getFileType()).isEqualTo("application/pdf");
        assertThat(savedFile.getValue().getUploadedAt()).isNotNull();
    }

    @Test
    void listMapsEveryStoredFile() {
        UploadedFile first = file(1L, "first.pdf");
        UploadedFile second = file(2L, "second.pdf");
        UploadedFileResponse firstResponse = response("first.pdf");
        UploadedFileResponse secondResponse = response("second.pdf");

        // The repository returns two files and the mapper returns their responses
        when(uploadedFileRepository.findAll()).thenReturn(List.of(first, second));
        when(uploadedFileMapper.toResponseDto(first)).thenReturn(firstResponse);
        when(uploadedFileMapper.toResponseDto(second)).thenReturn(secondResponse);

        assertThat(service.getAllFiles()).containsExactly(firstResponse, secondResponse);
    }

    @Test
    void getByIdReturnsMappedFile() {
        UploadedFile storedFile = file(3L, "report.pdf");
        UploadedFileResponse response = response("report.pdf");

        // The file exists and the mapper turns it into the expected response
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileMapper.toResponseDto(storedFile)).thenReturn(response);

        assertThat(service.getFileById(3L)).isSameAs(response);
    }

    @Test
    void getByIdRejectsUnknownFile() {
        // The repository returns empty because the file does not exist
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getFileById(3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verifyNoInteractions(uploadedFileMapper);
    }

    @Test
    void updateRenamesAndSavesFile() {
        UploadedFile storedFile = file(3L, "old.pdf");
        UploadedFileResponse response = response("new.pdf");

        // The file is found, saved with the new name and mapped to a response
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));
        when(uploadedFileRepository.save(storedFile)).thenReturn(storedFile);
        when(uploadedFileMapper.toResponseDto(storedFile)).thenReturn(response);

        assertThat(service.updateFile(3L, "new.pdf")).isSameAs(response);
        assertThat(storedFile.getOriginalFileName()).isEqualTo("new.pdf");
        verify(uploadedFileRepository).save(storedFile);
    }

    @Test
    void updateRejectsNullNameBeforeReadingDatabase() {
        assertThatThrownBy(() -> service.updateFile(3L, null))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST));
        verifyNoInteractions(uploadedFileRepository);
    }

    @Test
    void updateRejectsUnknownFile() {
        // The file does not exist, so save should not be called
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateFile(3L, "new.pdf"))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(uploadedFileRepository, never()).save(any());
    }

    @Test
    void deleteRemovesExistingFile() {
        UploadedFile storedFile = file(3L, "report.pdf");

        // The file exists, so the service can delete it
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.of(storedFile));

        service.deleteFile(3L);

        verify(uploadedFileRepository).delete(storedFile);
    }

    @Test
    void deleteRejectsUnknownFile() {
        // The file does not exist, so delete should not be called
        when(uploadedFileRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteFile(3L))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(error -> assertThat(((ResponseStatusException) error).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(uploadedFileRepository, never()).delete(any(UploadedFile.class));
    }

    private static UploadedFile file(Long id, String name) {
        return UploadedFile.builder().id(id).originalFileName(name).build();
    }

    private static UploadedFileResponse response(String name) {
        return UploadedFileResponse.builder().originalFileName(name).build();
    }
}
