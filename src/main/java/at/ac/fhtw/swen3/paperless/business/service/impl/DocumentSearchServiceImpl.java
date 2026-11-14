package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.model.DocumentSearchResult;
import at.ac.fhtw.swen3.paperless.business.service.DocumentSearchService;
import at.ac.fhtw.swen3.paperless.exception.InvalidInputException;
import at.ac.fhtw.swen3.paperless.exception.SearchException;
import at.ac.fhtw.swen3.paperless.dal.elasticsearch.IndexedDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentSearchServiceImpl implements DocumentSearchService {

    private final IndexedDocumentRepository indexedDocumentRepository;

    @Override
    public List<DocumentSearchResult> search(String query) {
        if (query == null || query.isBlank()) {
            throw new InvalidInputException("Search query must not be empty");
        }

        try {
            return indexedDocumentRepository.findByContentContaining(query.trim()).stream()
                    .map(document -> new DocumentSearchResult(document.getDocumentId(), document.getFileName()))
                    .toList();
        } catch (Exception exception) {
            throw new SearchException("Could not search documents", exception);
        }
    }
}
