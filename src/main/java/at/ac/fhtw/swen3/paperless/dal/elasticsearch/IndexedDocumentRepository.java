package at.ac.fhtw.swen3.paperless.dal.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface IndexedDocumentRepository extends ElasticsearchRepository<IndexedDocument, Long> {

    List<IndexedDocument> findByContentContaining(String query);
}
