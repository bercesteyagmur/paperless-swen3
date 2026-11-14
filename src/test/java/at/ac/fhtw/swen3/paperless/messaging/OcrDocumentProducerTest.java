package at.ac.fhtw.swen3.paperless.messaging;

import at.ac.fhtw.swen3.paperless.config.RabbitMqConfig;
import at.ac.fhtw.swen3.paperless.exception.MessagePublishException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OcrDocumentProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OcrDocumentProducer producer;

    @Test
    void sendsDocumentToOcrQueue() {
        producer.sendDocument(12L, "HelloWorld.pdf");

        OcrDocumentMessage message = new OcrDocumentMessage(12L, "HelloWorld.pdf");
        verify(rabbitTemplate).convertAndSend(RabbitMqConfig.OCR_QUEUE, message);
    }

    @Test
    void reportsErrorWhenMessageCannotBeSent() {
        AmqpException rabbitError = new AmqpException("RabbitMQ unavailable");
        OcrDocumentMessage message = new OcrDocumentMessage(12L, "HelloWorld.pdf");

        doThrow(rabbitError).when(rabbitTemplate).convertAndSend(
                RabbitMqConfig.OCR_QUEUE, message);

        MessagePublishException exception = assertThrows(
                MessagePublishException.class, () -> producer.sendDocument(12L, "HelloWorld.pdf"));

        assertThat(exception.getCause()).isSameAs(rabbitError);
    }
}
