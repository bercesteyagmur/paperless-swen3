package at.ac.fhtw.swen3.paperless.indexingworker.document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "documents")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IndexedDocument {

    @Id
    private Long documentId;

    private String fileName;
    private String content;
}
