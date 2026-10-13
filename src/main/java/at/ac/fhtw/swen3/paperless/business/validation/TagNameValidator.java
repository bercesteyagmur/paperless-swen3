package at.ac.fhtw.swen3.paperless.business.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TagNameValidator {

    public static String getValidTagName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tag name must not be empty");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tag name must not be longer than 50 characters");
        }

        return trimmedName;
    }
}
