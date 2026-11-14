package at.ac.fhtw.swen3.paperless.messaging;

public record OcrDocumentMessage(Long documentId, String fileName) {
}
