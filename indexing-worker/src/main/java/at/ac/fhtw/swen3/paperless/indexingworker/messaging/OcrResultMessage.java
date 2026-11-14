package at.ac.fhtw.swen3.paperless.indexingworker.messaging;

public record OcrResultMessage(
        Long documentId,
        String fileName,
        String content) {
}
