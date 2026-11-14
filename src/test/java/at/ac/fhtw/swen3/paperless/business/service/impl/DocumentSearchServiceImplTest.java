package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.model.DocumentSearchResult;
import at.ac.fhtw.swen3.paperless.exception.InvalidInputException;
import at.ac.fhtw.swen3.paperless.exception.SearchException;
import at.ac.fhtw.swen3.paperless.dal.elasticsearch.IndexedDocument;
import at.ac.fhtw.swen3.paperless.dal.elasticsearch.IndexedDocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DocumentSearchServiceImplTest {

    @Mock
    private IndexedDocumentRepository indexedDocumentRepository;

    @InjectMocks
    private DocumentSearchServiceImpl service;

    @Test
    void searchReturnsMatchingDocuments() {
        IndexedDocument document = new IndexedDocument(12L, "HelloWorld.pdf", "Hello World");
        when(indexedDocumentRepository.findByContentContaining("Hello")).thenReturn(List.of(document));

        List<DocumentSearchResult> result = service.search(" Hello ");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDocumentId()).isEqualTo(12L);
        assertThat(result.getFirst().getFileName()).isEqualTo("HelloWorld.pdf");
        verify(indexedDocumentRepository).findByContentContaining("Hello");
    }

    @Test
    void searchRejectsBlankQuery() {
        assertThatThrownBy(() -> service.search(" "))
                .isInstanceOf(InvalidInputException.class);

        verifyNoInteractions(indexedDocumentRepository);
    }

    @Test
    void searchReportsRepositoryFailure() {
        when(indexedDocumentRepository.findByContentContaining("Hello"))
                .thenThrow(new RuntimeException());

        assertThatThrownBy(() -> service.search("Hello"))
                .isInstanceOf(SearchException.class);
    }
}
