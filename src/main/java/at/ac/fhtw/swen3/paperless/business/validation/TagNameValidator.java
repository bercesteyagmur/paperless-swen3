package at.ac.fhtw.swen3.paperless.business.validation;

import at.ac.fhtw.swen3.paperless.exception.InvalidInputException;

public class TagNameValidator {

    public static String getValidTagName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidInputException("tag name must not be empty");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > 50) {
            throw new InvalidInputException("tag name must not be longer than 50 characters");
        }

        return trimmedName;
    }
}
