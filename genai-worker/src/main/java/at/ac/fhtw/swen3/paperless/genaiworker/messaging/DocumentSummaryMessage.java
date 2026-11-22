package at.ac.fhtw.swen3.paperless.genaiworker.messaging;

public record DocumentSummaryMessage(
        Long documentId,
        String summary) {
}
