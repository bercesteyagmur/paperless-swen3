package at.ac.fhtw.swen3.paperless.ocrworker.messaging.outgoing;

import at.ac.fhtw.swen3.paperless.ocrworker.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OcrResultProducer {

    private static final Logger logger = LoggerFactory.getLogger(OcrResultProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public void sendResult(OcrResultMessage result) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.OCR_RESULT_QUEUE, result);
        logger.info("Sent OCR result for document {}", result.documentId());
    }
}
