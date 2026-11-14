package at.ac.fhtw.swen3.paperless.business.service;

import at.ac.fhtw.swen3.paperless.business.model.DocumentSearchResult;

import java.util.List;

public interface DocumentSearchService {

    List<DocumentSearchResult> search(String query);
}
