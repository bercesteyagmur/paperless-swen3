package at.ac.fhtw.swen3.paperless.controller;

import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.business.service.TagService;
import at.ac.fhtw.swen3.paperless.dto.TagRequest;
import at.ac.fhtw.swen3.paperless.dto.TagResponse;
import at.ac.fhtw.swen3.paperless.dto.UploadedFileResponse;
import at.ac.fhtw.swen3.paperless.mapper.TagMapper;
import at.ac.fhtw.swen3.paperless.mapper.UploadedFileMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagMapper tagMapper;
    private final UploadedFileMapper uploadedFileMapper;

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
        TagModel tagModel = TagModel.builder()
                .name(request.getName())
                .build();

        TagModel savedTag = tagService.createTag(tagModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(tagMapper.toResponseDto(savedTag));
    }

    @GetMapping
    public List<TagResponse> getAllTags() {
        return tagService.getAllTags().stream()
                .map(tagMapper::toResponseDto)
                .toList();
    }

    @GetMapping("/{id}")
    public TagResponse getTagById(@PathVariable Long id) {
        return tagMapper.toResponseDto(tagService.getTagById(id));
    }

    @GetMapping("/{tagId}/files")
    public List<UploadedFileResponse> getFilesForTag(@PathVariable Long tagId) {
        return tagService.getFilesForTag(tagId).stream()
                .map(uploadedFileMapper::toResponseDto)
                .toList();
    }

    @PatchMapping("/{id}")
    public TagResponse updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
        TagModel tagModel = TagModel.builder()
                .name(request.getName())
                .build();

        return tagMapper.toResponseDto(tagService.updateTag(id, tagModel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
