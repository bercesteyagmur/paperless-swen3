package at.ac.fhtw.swen3.paperless.indexingworker.repository;

import at.ac.fhtw.swen3.paperless.indexingworker.document.IndexedDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IndexedDocumentRepository extends ElasticsearchRepository<IndexedDocument, Long> {
}
