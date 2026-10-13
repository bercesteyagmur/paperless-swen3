package at.ac.fhtw.swen3.paperless.business.validation;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FileNameValidator {

    public static String getValidFileName(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name must not be empty");
        }

        String trimmedName = fileName.trim();
        if (trimmedName.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File name must not be longer than 100 characters");
        }

        if (!trimmedName.toLowerCase().endsWith(".pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only PDF files are allowed");
        }

        return trimmedName;
    }
}
