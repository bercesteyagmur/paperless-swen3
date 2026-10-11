package at.ac.fhtw.swen3.paperless.business.validation;

import at.ac.fhtw.swen3.paperless.business.exception.InvalidInputException;

public class FileNameValidator {

    public static String getValidFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidInputException("File name must not be empty");
        }

        String trimmedName = fileName.trim();
        if (trimmedName.length() > 100) {
            throw new InvalidInputException("File name must not be longer than 100 characters");
        }

        if (!trimmedName.toLowerCase().endsWith(".pdf")) {
            throw new InvalidInputException("Only PDF files are allowed");
        }

        return trimmedName;
    }
}
