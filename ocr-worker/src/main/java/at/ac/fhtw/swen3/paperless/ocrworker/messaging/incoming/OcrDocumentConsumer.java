package at.ac.fhtw.swen3.paperless.ocrworker.messaging.incoming;

import at.ac.fhtw.swen3.paperless.ocrworker.config.RabbitMqConfig;
import at.ac.fhtw.swen3.paperless.ocrworker.messaging.outgoing.OcrResultMessage;
import at.ac.fhtw.swen3.paperless.ocrworker.messaging.outgoing.OcrResultProducer;
import at.ac.fhtw.swen3.paperless.ocrworker.service.OcrService;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class OcrDocumentConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OcrDocumentConsumer.class);

    private final OcrService ocrService;
    private final OcrResultProducer ocrResultProducer;
    private final MinioClient minioClient;

    @RabbitListener(queues = RabbitMqConfig.OCR_QUEUE)
    public void receiveDocument(OcrDocumentMessage message) {
        Long documentId = message.documentId();
        String fileName = message.fileName();
        String id = documentId.toString();

        logger.info("Received document {} from the queue", documentId);
        File pdf = new File(id + ".pdf");

        try {
            DownloadObjectArgs args = DownloadObjectArgs.builder()
                    .bucket("documents")
                    .object(id)
                    .filename(pdf.getPath())
                    .build();

            minioClient.downloadObject(args);

            String text = ocrService.extractText(pdf);

            OcrResultMessage result = new OcrResultMessage(documentId, fileName, text);
            ocrResultProducer.sendResult(result);

            logger.info("Extracted text from document {}", id);

        } catch (Exception exception) {
            logger.error("Could not read the text of document {}", id, exception);
        } finally {
            pdf.delete();
        }
    }
}
