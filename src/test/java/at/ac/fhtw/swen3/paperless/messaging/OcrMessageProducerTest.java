package at.ac.fhtw.swen3.paperless.messaging;

import at.ac.fhtw.swen3.paperless.config.RabbitMqConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OcrMessageProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OcrMessageProducer producer;

    @Test
    void sendsDocumentToOcrQueue() {
        producer.sendDocument(12L, "HelloWorld.pdf");

        verify(rabbitTemplate).convertAndSend(
                RabbitMqConfig.OCR_QUEUE,
                "12:HelloWorld.pdf"
        );
    }
}
