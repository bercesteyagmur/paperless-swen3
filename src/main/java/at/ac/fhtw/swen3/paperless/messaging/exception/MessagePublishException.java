package at.ac.fhtw.swen3.paperless.messaging.exception;

public class MessagePublishException extends RuntimeException {

    public MessagePublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
