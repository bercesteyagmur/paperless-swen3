package at.ac.fhtw.swen3.paperless.controller;

import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileUpdateRequest;
import at.ac.fhtw.swen3.paperless.mapper.UploadedFileMapper;
import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;

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
    private final UploadedFileMapper uploadedFileMapper;

    // POST /api/files (multipart/form-data, part name "file") -> 201 Created
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UploadedFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        UploadedFileModel uploadedFileModel = UploadedFileModel.builder()
                .originalFileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .build();

        UploadedFileModel savedUploadedFileModel = uploadedFileService.uploadFile(uploadedFileModel);
        UploadedFileResponse response = uploadedFileMapper.toResponseDto(savedUploadedFileModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/files -> default 200, list of all files
    @GetMapping
    public List<UploadedFileResponse> getAllFiles() {
        return uploadedFileService.getAllFiles().stream()
                .map(uploadedFileMapper::toResponseDto)
                .toList();
    }

    // GET /api/files/{id} -> 200 OK or 404 (thrown in Service)
    @GetMapping("/{id}")
    public UploadedFileResponse getFileById(@PathVariable Long id) {
        return uploadedFileMapper.toResponseDto(uploadedFileService.getFileById(id));
    }

    // PATCH /api/files/{id}, body: {"originalFileName": "new.pdf"} -> 200
    @PatchMapping("/{id}")
    public UploadedFileResponse updateFile(@PathVariable Long id, @RequestBody UploadedFileUpdateRequest request) {
        UploadedFileModel updatedUploadedFileModel = uploadedFileService.updateFile(id, request.getOriginalFileName());
        return uploadedFileMapper.toResponseDto(updatedUploadedFileModel);
    }

    // DELETE /api/files/{id} -> 204
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        uploadedFileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
}
