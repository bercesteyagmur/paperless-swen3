package at.ac.fhtw.swen3.paperless.genaiworker.repository;

import at.ac.fhtw.swen3.paperless.genaiworker.document.IndexedDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IndexedDocumentRepository extends ElasticsearchRepository<IndexedDocument, Long> {
}
