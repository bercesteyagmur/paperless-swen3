package at.ac.fhtw.swen3.paperless.genaiworker.messaging;

import at.ac.fhtw.swen3.paperless.genaiworker.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentSummaryProducer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSummaryProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void sendSummary(Long documentId, String summary) {
        DocumentSummaryMessage message = new DocumentSummaryMessage(documentId, summary);

        rabbitTemplate.convertAndSend(RabbitMqConfig.DOCUMENT_SUMMARY_QUEUE, message);
        logger.info("Sent summary for document {}", documentId);
    }
}
