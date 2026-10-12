package at.ac.fhtw.swen3.paperless.messaging;

import at.ac.fhtw.swen3.paperless.config.RabbitMqConfig;
import at.ac.fhtw.swen3.paperless.exception.MessagePublishException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OcrMessageProducer {

    private static final Logger logger = LoggerFactory.getLogger(OcrMessageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void sendDocument(Long documentId, String fileName) {
        String message = documentId + ":" + fileName;

        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.OCR_QUEUE, message);
            logger.info("Sent document {} to OCR queue", documentId);

        } catch (AmqpException exception) {
            logger.error("Could not send document {} to OCR queue", documentId, exception);
            throw new MessagePublishException("Could not send document to OCR queue", exception);
        }
    }
}
