package at.ac.fhtw.swen3.paperless.exception;

public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String message) {

        super(message);
    }
}
