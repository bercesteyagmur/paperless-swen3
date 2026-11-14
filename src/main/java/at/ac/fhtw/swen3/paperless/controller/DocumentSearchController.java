package at.ac.fhtw.swen3.paperless.controller;

import at.ac.fhtw.swen3.paperless.business.service.DocumentSearchService;
import at.ac.fhtw.swen3.paperless.dto.DocumentSearchResponse;
import at.ac.fhtw.swen3.paperless.mapper.DocumentSearchMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class DocumentSearchController {

    private final DocumentSearchService documentSearchService;
    private final DocumentSearchMapper documentSearchMapper;

    @GetMapping("/search")
    public List<DocumentSearchResponse> search(@RequestParam String query) {
        return documentSearchService.search(query).stream()
                .map(documentSearchMapper::toResponseDto)
                .toList();
    }
}
