package at.ac.fhtw.swen3.paperless.controller;

import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.business.service.TagService;
import at.ac.fhtw.swen3.paperless.business.service.UploadedFileService;
import at.ac.fhtw.swen3.paperless.dto.TagRequest;
import at.ac.fhtw.swen3.paperless.dto.TagResponse;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileUpdateRequest;
import at.ac.fhtw.swen3.paperless.mapper.TagMapper;
import at.ac.fhtw.swen3.paperless.mapper.UploadedFileMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor

public class UploadedFileController {

    private final UploadedFileService uploadedFileService;
    private final UploadedFileMapper uploadedFileMapper;
    private final TagService tagService;
    private final TagMapper tagMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<UploadedFileResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must not be empty");
        }

        UploadedFileModel uploadedFileModel = UploadedFileModel.builder()
                .originalFileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .build();

        UploadedFileModel savedUploadedFileModel = uploadedFileService.uploadFile(uploadedFileModel);
        UploadedFileResponse response = uploadedFileMapper.toResponseDto(savedUploadedFileModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<UploadedFileResponse> getAllFiles() {
        return uploadedFileService.getAllFiles().stream()
                .map(uploadedFileMapper::toResponseDto)
                .toList();
    }

    @GetMapping("/{id}")
    public UploadedFileResponse getFileById(@PathVariable Long id) {
        return uploadedFileMapper.toResponseDto(uploadedFileService.getFileById(id));
    }

    @PatchMapping("/{id}")
    public UploadedFileResponse updateFile(@PathVariable Long id, @Valid @RequestBody UploadedFileUpdateRequest request) {
        UploadedFileModel updatedUploadedFileModel = uploadedFileService.updateFile(id, request.getNewFileName());
        return uploadedFileMapper.toResponseDto(updatedUploadedFileModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long id) {
        uploadedFileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{fileId}/tags")
    public UploadedFileResponse addTag(@PathVariable Long fileId, @Valid @RequestBody TagRequest request) {
        UploadedFileModel updatedFile = tagService.addTag(fileId, request.getName());
        return uploadedFileMapper.toResponseDto(updatedFile);
    }

    @DeleteMapping("/{fileId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromFile(@PathVariable Long fileId, @PathVariable Long tagId) {
        tagService.removeTagFromFile(fileId, tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{fileId}/tags")
    public List<TagResponse> getTagsForFile(@PathVariable Long fileId) {
        return tagService.getTagsForFile(fileId).stream()
                .map(tagMapper::toResponseDto)
                .toList();
    }
}
