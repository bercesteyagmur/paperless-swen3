package at.ac.fhtw.swen3.paperless.messaging;

import at.ac.fhtw.swen3.paperless.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OcrMessageProducer {

    private final RabbitTemplate rabbitTemplate;


    public void sendDocument(Long documentId, String fileName) {
        String message = documentId + " : " + fileName;

        rabbitTemplate.convertAndSend(RabbitMqConfig.OCR_QUEUE, message);
    }
}
