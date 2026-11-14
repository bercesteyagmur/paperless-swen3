package at.ac.fhtw.swen3.paperless.ocrworker.messaging;

public record OcrDocumentMessage(Long documentId, String fileName) {
}
