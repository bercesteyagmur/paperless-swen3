package at.ac.fhtw.swen3.paperless.storage;

import at.ac.fhtw.swen3.paperless.exception.FileStorageException;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Component
public class MinioService {

    private static final Logger logger = LoggerFactory.getLogger(MinioService.class);
    private static final String BUCKET = "documents";

    private final MinioClient minioClient;

    public MinioService(@Value("${minio.url}") String url,
                        @Value("${minio.user}") String user,
                        @Value("${minio.password}") String password) {
        minioClient = MinioClient.builder()
                .endpoint(url)
                .credentials(user, password)
                .build();
    }

    public void uploadFile(String objectName, MultipartFile file) {
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(BUCKET).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET).build());
            }

            File tempFile = File.createTempFile("upload", ".pdf");
            file.transferTo(tempFile);

            minioClient.uploadObject(
                    UploadObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(objectName)
                            .filename(tempFile.getAbsolutePath())
                            .contentType(file.getContentType())
                            .build()
            );
            tempFile.delete();
            logger.info("Stored file {} in MinIO", objectName);

        } catch (Exception exception) {
            logger.error("Could not store file {} in MinIO", objectName, exception);
            throw new FileStorageException("Could not store file in MinIO", exception);
        }
    }
}
