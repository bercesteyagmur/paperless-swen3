package at.ac.fhtw.swen3.paperless.messaging;

import at.ac.fhtw.swen3.paperless.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentSummaryConsumer {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSummaryConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.DOCUMENT_SUMMARY_QUEUE)
    public void receiveSummary(DocumentSummaryMessage message) {
        logger.info("Received summary for document {}", message.documentId());
    }
}
