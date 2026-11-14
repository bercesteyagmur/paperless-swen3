package at.ac.fhtw.swen3.paperless.storage;

import at.ac.fhtw.swen3.paperless.exception.FileStorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioService minioService;

    @Test
    void uploadCreatesMissingBucketAndStoresPdf() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "pdf content".getBytes());
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);


        minioService.uploadFile("12", file);


        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        ArgumentCaptor<UploadObjectArgs> uploadCaptor = ArgumentCaptor.forClass(UploadObjectArgs.class);
        verify(minioClient).uploadObject(uploadCaptor.capture());

        UploadObjectArgs upload = uploadCaptor.getValue();
        assertThat(upload.bucket()).isEqualTo("documents");
        assertThat(upload.object()).isEqualTo("12");
        assertThat(upload.contentType()).isEqualTo("application/pdf");
    }

    @Test
    void failedUploadDeletesTemporaryFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "pdf content".getBytes());
        ArgumentCaptor<UploadObjectArgs> uploadCaptor = ArgumentCaptor.forClass(UploadObjectArgs.class);

        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.uploadObject(uploadCaptor.capture())).thenThrow(new RuntimeException("MinIO unavailable"));

        assertThrows(FileStorageException.class, () -> minioService.uploadFile("12", file));

        assertThat(new File(uploadCaptor.getValue().filename())).doesNotExist();
    }
}
