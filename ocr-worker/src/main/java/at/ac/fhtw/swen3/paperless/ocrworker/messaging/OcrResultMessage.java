package at.ac.fhtw.swen3.paperless.ocrworker.messaging;

public record OcrResultMessage(
        Long documentId,
        String fileName,
        String content) {
}
