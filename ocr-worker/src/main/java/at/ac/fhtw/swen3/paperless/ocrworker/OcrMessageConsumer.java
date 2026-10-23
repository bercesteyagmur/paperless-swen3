package at.ac.fhtw.swen3.paperless.ocrworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OcrMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OcrMessageConsumer.class);

    @RabbitListener(queues = RabbitMqConfig.OCR_QUEUE)
    public void receiveDocument(String message) {
        logger.info("Received document for OCR: {}", message);
    }
}
