package at.ac.fhtw.swen3.paperless.genaiworker.messaging;

public record OcrResultMessage(
        Long documentId,
        String fileName,
        String content) {
}
