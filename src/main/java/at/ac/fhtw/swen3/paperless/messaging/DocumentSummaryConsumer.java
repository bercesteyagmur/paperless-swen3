package at.ac.fhtw.swen3.paperless.messaging;

import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;
import at.ac.fhtw.swen3.paperless.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentSummaryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSummaryConsumer.class);
    private final UploadedFileService uploadedFileService;

    @RabbitListener(queues = RabbitMqConfig.DOCUMENT_SUMMARY_QUEUE)
    public void receiveSummary(DocumentSummaryMessage message) {
        uploadedFileService.saveSummary(message.documentId(), message.summary());
        logger.info("Received summary for document {}", message.documentId());
    }
}
