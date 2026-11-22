package at.ac.fhtw.swen3.paperless.genaiworker.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String OCR_RESULT_QUEUE = "ocr.results";
    public static final String DOCUMENT_SUMMARY_QUEUE = "document.summaries";

    @Bean
    public Queue ocrResultQueue() {
        return new Queue(OCR_RESULT_QUEUE, true);
    }

    @Bean
    public Queue documentSummaryQueue() {
        return new Queue(DOCUMENT_SUMMARY_QUEUE, true);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
