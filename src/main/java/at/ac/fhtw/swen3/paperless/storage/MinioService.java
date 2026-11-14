package at.ac.fhtw.swen3.paperless.storage;

import at.ac.fhtw.swen3.paperless.exception.FileStorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import io.minio.UploadObjectArgs;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
@RequiredArgsConstructor
public class MinioService {

    private static final String BUCKET = "documents";

    private final MinioClient minioClient;

    public void uploadFile(String objectName, MultipartFile file) {
        File pdf = null;

        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
            }

            pdf = File.createTempFile("upload", ".pdf");
            file.transferTo(pdf);

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(objectName)
                            .filename(pdf.getAbsolutePath())
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception exception) {
            throw new FileStorageException("Could not store file in MinIO", exception);
        } finally {
            if (pdf != null) {
                pdf.delete();
            }
        }
    }
}
