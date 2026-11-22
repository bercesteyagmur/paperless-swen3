package at.ac.fhtw.swen3.paperless.genaiworker.messaging;

import at.ac.fhtw.swen3.paperless.genaiworker.config.RabbitMqConfig;
import at.ac.fhtw.swen3.paperless.genaiworker.document.IndexedDocument;
import at.ac.fhtw.swen3.paperless.genaiworker.repository.IndexedDocumentRepository;
import at.ac.fhtw.swen3.paperless.genaiworker.service.GeminiService;
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
    private final GeminiService geminiService;
    private final DocumentSummaryProducer documentSummaryProducer;

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


        //
        String summary = geminiService.summarize(result.content());
        logger.info("Generated summary for document {}: {}", result.documentId(), summary);

        documentSummaryProducer.sendSummary(result.documentId(), summary);
    }
}
