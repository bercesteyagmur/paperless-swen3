package at.ac.fhtw.swen3.paperless.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Bean
    public MinioClient minioClient(@Value("${minio.url}") String url,
                                   @Value("${minio.user}") String user,
                                   @Value("${minio.password}") String password) {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(user, password)
                .build();
    }
}
