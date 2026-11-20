package at.ac.fhtw.swen3.paperless.indexingworker.messaging;

import at.ac.fhtw.swen3.paperless.indexingworker.document.IndexedDocument;
import at.ac.fhtw.swen3.paperless.indexingworker.repository.IndexedDocumentRepository;
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

    @Test
    void storesOcrResultInElasticsearch() {
        OcrResultConsumer consumer = new OcrResultConsumer(indexedDocumentRepository);
        OcrResultMessage result = new OcrResultMessage(12L, "report.pdf", "Document text");

        consumer.receiveResult(result);

        ArgumentCaptor<IndexedDocument> document = ArgumentCaptor.forClass(IndexedDocument.class);
        verify(indexedDocumentRepository).save(document.capture());
        assertThat(document.getValue().getDocumentId()).isEqualTo(12L);
        assertThat(document.getValue().getFileName()).isEqualTo("report.pdf");
        assertThat(document.getValue().getContent()).isEqualTo("Document text");
    }
}
