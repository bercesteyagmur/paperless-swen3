package at.ac.fhtw.swen3.paperless.ocrworker;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String OCR_QUEUE = "ocr.documents";

    @Bean
    public Queue ocrQueue() {
        return new Queue(OCR_QUEUE, true);
    }
}
