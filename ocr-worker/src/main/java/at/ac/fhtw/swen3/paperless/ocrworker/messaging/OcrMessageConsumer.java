package at.ac.fhtw.swen3.paperless.ocrworker.messaging;

import at.ac.fhtw.swen3.paperless.ocrworker.config.RabbitMqConfig;
import at.ac.fhtw.swen3.paperless.ocrworker.service.OcrService;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class OcrMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OcrMessageConsumer.class);

    private final OcrService ocrService;

    @Value("${minio.url}")
    private String url;

    @Value("${minio.user}")
    private String user;

    @Value("${minio.password}")
    private String password;

    @RabbitListener(queues = RabbitMqConfig.OCR_QUEUE)
    public void receiveDocument(String message) {
        logger.info("New message from the queue: {}", message);

        String id = message.split(":")[0];
        File pdf = new File(id + ".pdf");

        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(url)
                    .credentials(user, password)
                    .build();

            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket("documents")
                            .object(id)
                            .filename(pdf.getPath())
                            .build()
            );

            String text = ocrService.extractText(pdf);
            logger.info("Text of document {}: {}", id, text);

        } catch (Exception exception) {
            logger.error("Could not read the text of document {}", id, exception);
        }

        pdf.delete();
    }
}
