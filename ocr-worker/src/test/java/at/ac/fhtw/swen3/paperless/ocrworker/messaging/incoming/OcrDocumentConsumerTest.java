package at.ac.fhtw.swen3.paperless.ocrworker.messaging.incoming;

import at.ac.fhtw.swen3.paperless.ocrworker.messaging.outgoing.OcrResultMessage;
import at.ac.fhtw.swen3.paperless.ocrworker.messaging.outgoing.OcrResultProducer;
import at.ac.fhtw.swen3.paperless.ocrworker.service.OcrService;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcrDocumentConsumerTest {

    @Mock
    private OcrService ocrService;

    @Mock
    private OcrResultProducer ocrResultProducer;

    @Mock
    private MinioClient minioClient;

    private OcrDocumentConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new OcrDocumentConsumer(ocrService, ocrResultProducer, minioClient);
    }

    @Test
    void downloadsDocumentFromMinio() throws Exception {
        when(ocrService.extractText(any(File.class))).thenReturn("Document text");

        consumer.receiveDocument(new OcrDocumentMessage(987654321L, "report.pdf"));

        ArgumentCaptor<DownloadObjectArgs> arguments = ArgumentCaptor.forClass(DownloadObjectArgs.class);
        verify(minioClient).downloadObject(arguments.capture());
        assertThat(arguments.getValue().bucket()).isEqualTo("documents");
        assertThat(arguments.getValue().object()).isEqualTo("987654321");
    }

    @Test
    void publishesOcrResultAfterProcessing() throws Exception {
        when(ocrService.extractText(any(File.class))).thenReturn("Document text");

        consumer.receiveDocument(new OcrDocumentMessage(987654321L, "report.pdf"));

        verify(ocrResultProducer).sendResult(
                new OcrResultMessage(987654321L, "report.pdf", "Document text")
        );
    }

    @Test
    void doesNotPublishResultWhenOcrFails() throws Exception {
        when(ocrService.extractText(any(File.class))).thenThrow(new IllegalStateException("OCR failed"));

        consumer.receiveDocument(new OcrDocumentMessage(987654321L, "report.pdf"));

        verify(ocrResultProducer, never()).sendResult(any(OcrResultMessage.class));
    }
}
