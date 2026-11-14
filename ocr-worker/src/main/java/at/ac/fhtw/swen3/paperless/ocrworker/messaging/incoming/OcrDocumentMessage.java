package at.ac.fhtw.swen3.paperless.ocrworker.messaging.incoming;

public record OcrDocumentMessage(Long documentId, String fileName) {
}
