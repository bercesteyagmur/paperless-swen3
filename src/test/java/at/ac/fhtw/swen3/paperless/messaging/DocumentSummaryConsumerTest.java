package at.ac.fhtw.swen3.paperless.messaging;

import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DocumentSummaryConsumerTest {

    @Mock
    private UploadedFileService uploadedFileService;

    @InjectMocks
    private DocumentSummaryConsumer consumer;

    @Test
    void passesReceivedSummaryToService() {
        consumer.receiveSummary(new DocumentSummaryMessage(3L, "Short document summary"));

        verify(uploadedFileService).saveSummary(3L, "Short document summary");
    }
}
