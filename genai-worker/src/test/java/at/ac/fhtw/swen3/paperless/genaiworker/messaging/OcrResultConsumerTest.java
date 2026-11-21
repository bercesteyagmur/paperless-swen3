package at.ac.fhtw.swen3.paperless.genaiworker.messaging;

import at.ac.fhtw.swen3.paperless.genaiworker.document.IndexedDocument;
import at.ac.fhtw.swen3.paperless.genaiworker.repository.IndexedDocumentRepository;
import at.ac.fhtw.swen3.paperless.genaiworker.service.GeminiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OcrResultConsumerTest {

    @Mock
    private IndexedDocumentRepository indexedDocumentRepository;

    @Mock
    private GeminiService geminiService;

    @Test
    void storesOcrResultInElasticsearch() {
        OcrResultConsumer consumer = new OcrResultConsumer(indexedDocumentRepository, geminiService);
        OcrResultMessage result = new OcrResultMessage(12L, "report.pdf", "Document text");

        consumer.receiveResult(result);

        ArgumentCaptor<IndexedDocument> document = ArgumentCaptor.forClass(IndexedDocument.class);
        verify(indexedDocumentRepository).save(document.capture());
        assertThat(document.getValue().getDocumentId()).isEqualTo(12L);
        assertThat(document.getValue().getFileName()).isEqualTo("report.pdf");
        assertThat(document.getValue().getContent()).isEqualTo("Document text");
    }

    @Test
    void sendsOcrTextToGemini() {
        OcrResultConsumer consumer = new OcrResultConsumer(indexedDocumentRepository, geminiService);
        OcrResultMessage result = new OcrResultMessage(
                12L,
                "report.pdf",
                "This document explains the project architecture."
        );

        consumer.receiveResult(result);

        verify(geminiService).summarize("This document explains the project architecture.");
    }
}
