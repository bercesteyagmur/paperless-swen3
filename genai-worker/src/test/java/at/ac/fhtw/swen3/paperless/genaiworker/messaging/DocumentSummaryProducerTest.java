package at.ac.fhtw.swen3.paperless.genaiworker.messaging;

import at.ac.fhtw.swen3.paperless.genaiworker.config.RabbitMqConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentSummaryProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private DocumentSummaryProducer producer;

    @Test
    void sendsSummaryToDocumentSummaryQueue() {
        producer.sendSummary(12L, "Short summary");

        DocumentSummaryMessage message = new DocumentSummaryMessage(12L, "Short summary");
        verify(rabbitTemplate).convertAndSend(RabbitMqConfig.DOCUMENT_SUMMARY_QUEUE, message);
    }
}
