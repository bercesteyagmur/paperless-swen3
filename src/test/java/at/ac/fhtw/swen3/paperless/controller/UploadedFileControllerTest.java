package at.ac.fhtw.swen3.paperless.controller;

import at.ac.fhtw.swen3.paperless.business.service.TagService;
import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;
import at.ac.fhtw.swen3.paperless.mapper.TagMapper;
import at.ac.fhtw.swen3.paperless.mapper.UploadedFileMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UploadedFileControllerTest {

    @Mock
    private UploadedFileService uploadedFileService;

    @Mock
    private UploadedFileMapper uploadedFileMapper;

    @Mock
    private TagService tagService;

    @Mock
    private TagMapper tagMapper;

    @InjectMocks
    private UploadedFileController controller;

    @Test
    void uploadRejectsEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "report.pdf",
                "application/pdf",
                new byte[0]
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> controller.uploadFile(file)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(uploadedFileService, never()).uploadFile(any());
    }

}
