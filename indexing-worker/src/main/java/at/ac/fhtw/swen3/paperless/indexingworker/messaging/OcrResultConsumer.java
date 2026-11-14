package at.ac.fhtw.swen3.paperless.indexingworker.messaging;

import at.ac.fhtw.swen3.paperless.indexingworker.config.RabbitMqConfig;
import at.ac.fhtw.swen3.paperless.indexingworker.document.IndexedDocument;
import at.ac.fhtw.swen3.paperless.indexingworker.repository.IndexedDocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OcrResultConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OcrResultConsumer.class);

    private final IndexedDocumentRepository indexedDocumentRepository;

    @RabbitListener(queues = RabbitMqConfig.OCR_RESULT_QUEUE)

    public void receiveResult(OcrResultMessage result) {
        logger.info("Received OCR result for document {}", result.documentId());

        IndexedDocument document = new IndexedDocument(
                result.documentId(),
                result.fileName(),
                result.content()
        );

        indexedDocumentRepository.save(document);
        logger.info("Indexed document {} in Elasticsearch", result.documentId());
    }
}
