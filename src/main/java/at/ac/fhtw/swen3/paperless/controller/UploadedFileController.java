package at.ac.fhtw.swen3.paperless.controller;

import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileUpdateRequest;
import at.ac.fhtw.swen3.paperless.service.UploadedFileService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// REST endpoints for /api/files. Only talks to the Service, never with Repository directly!!!!!!
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class UploadedFileController {

    private final UploadedFileService uploadedFileService;

    // POST /api/files?userId=1 (multipart/form-data, part name "file") -> 201 Created
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UploadedFileResponse> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam Long userId) {
        UploadedFileResponse response = uploadedFileService.uploadFile(file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/files -> default 200, list of all files
    @GetMapping
    public List<UploadedFileResponse> getAllFiles() {
        return uploadedFileService.getAllFiles();
    }

    // GET /api/files/{id} -> 200 OK or 404 (thrown in Service)
    @GetMapping("/{id}")
    public UploadedFileResponse getFileById(@PathVariable Long id) {
        return uploadedFileService.getFileById(id);
    }

    // PATCH /api/files/{id}, body: {"originalFileName": "new.pdf"} -> 200
    @PatchMapping("/{id}")
    public UploadedFileResponse updateFile(@PathVariable Long id, @RequestBody UploadedFileUpdateRequest request) {
        return uploadedFileService.updateFile(id, request.getOriginalFileName());
    }

    // DELETE /api/files/{id} -> 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        uploadedFileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}
