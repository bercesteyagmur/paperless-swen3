package at.ac.fhtw.swen3.paperless.indexingworker.messaging;

import at.ac.fhtw.swen3.paperless.indexingworker.config.RabbitMqConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OcrResultConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OcrResultConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.OCR_RESULT_QUEUE)

    public void receiveResult(OcrResultMessage result) {
        logger.info("Received OCR result for document {}", result.documentId());
    }
}
