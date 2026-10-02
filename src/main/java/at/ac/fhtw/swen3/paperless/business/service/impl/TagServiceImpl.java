package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.mapper.TagModelMapper;
import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.business.service.TagService;
import at.ac.fhtw.swen3.paperless.dal.entity.Tag;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.TagRepository;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagModelMapper tagModelMapper;
    private final UploadedFileRepository uploadedFileRepository;
    private final UploadedFileModelMapper uploadedFileModelMapper;

    @Override
    public TagModel createTag(TagModel tagModel) {
        String name = getValidName(tagModel);
        if (tagRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "tag already exists");
        }

        tagModel.setName(name);
        Tag savedTag = tagRepository.save(tagModelMapper.toEntity(tagModel));
        return tagModelMapper.toModel(savedTag);
    }

    @Override
    public List<TagModel> getAllTags() {
        return tagRepository.findAll().stream()
                .map(tagModelMapper::toModel)
                .toList();
    }

    @Override
    public TagModel getTagById(Long id) {
        return tagModelMapper.toModel(findTagByIdOrThrow(id));
    }

    @Override
    public TagModel updateTag(Long id, TagModel tagModel) {
        Tag tag = findTagByIdOrThrow(id);
        String name = getValidName(tagModel);

        if (!tag.getName().equalsIgnoreCase(name) && tagRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "tag already exists");
        }

        tag.setName(name);
        return tagModelMapper.toModel(tagRepository.save(tag));
    }

    @Override
    public void deleteTag(Long id) {
        Tag tag = findTagByIdOrThrow(id);
        List<UploadedFile> files = uploadedFileRepository.findAllByTags_Id(id);

        files.forEach(file -> file.getTags().removeIf(fileTag -> fileTag.getId().equals(id)));
        uploadedFileRepository.saveAll(files);
        tagRepository.delete(tag);
    }


    @Override
    public void addTagToFile(Long fileId, Long tagId) {
        UploadedFile file = findFileByIdOrThrow(fileId);
        Tag tag = findTagByIdOrThrow(tagId);

        boolean alreadyAssigned = file.getTags().stream()
                .anyMatch(fileTag -> fileTag.getId().equals(tagId));

        if (alreadyAssigned) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "tag is already assigned to file");
        }

        file.getTags().add(tag);
        uploadedFileRepository.save(file);
    }

    @Override
    public void removeTagFromFile(Long fileId, Long tagId) {
        UploadedFile file = findFileByIdOrThrow(fileId);
        findTagByIdOrThrow(tagId);

        boolean removed = file.getTags().removeIf(fileTag -> fileTag.getId().equals(tagId));

        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "tag is not assigned to file");
        }

        uploadedFileRepository.save(file);
    }

    @Override
    public List<TagModel> getTagsForFile(Long fileId) {
        return findFileByIdOrThrow(fileId).getTags().stream()
                .map(tagModelMapper::toModel)
                .toList();
    }

    @Override
    public List<UploadedFileModel> getFilesForTag(Long tagId) {
        findTagByIdOrThrow(tagId);
        return uploadedFileRepository.findAllByTags_Id(tagId).stream()
                .map(uploadedFileModelMapper::toModel)
                .toList();
    }

    private String getValidName(TagModel tagModel) {
        if (tagModel == null || tagModel.getName() == null || tagModel.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tag name must not be empty");
        }
        return tagModel.getName().trim();
    }

    private Tag findTagByIdOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "tag not found"));
    }

    private UploadedFile findFileByIdOrThrow(Long id) {
        return uploadedFileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found"));
    }
}
