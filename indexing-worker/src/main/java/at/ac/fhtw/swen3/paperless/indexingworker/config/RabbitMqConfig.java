package at.ac.fhtw.swen3.paperless.indexingworker.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String OCR_RESULT_QUEUE = "ocr.results";

    @Bean
    public Queue ocrResultQueue() {
        return new Queue(OCR_RESULT_QUEUE, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
