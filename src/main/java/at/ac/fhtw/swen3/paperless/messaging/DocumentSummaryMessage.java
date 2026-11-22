package at.ac.fhtw.swen3.paperless.messaging;

public record DocumentSummaryMessage(
        Long documentId,
        String summary) {
}
