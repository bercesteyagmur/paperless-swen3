package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.exception.ResourceAlreadyExistsException;
import at.ac.fhtw.swen3.paperless.business.exception.ResourceNotFoundException;
import at.ac.fhtw.swen3.paperless.business.mapper.TagModelMapper;
import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.business.service.TagService;
import at.ac.fhtw.swen3.paperless.business.validation.TagNameValidator;
import at.ac.fhtw.swen3.paperless.dal.entity.Tag;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.TagRepository;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        String name = TagNameValidator.getValidTagName(tagModel.getName());
        checkTagNameIsFree(name);

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
        String name = TagNameValidator.getValidTagName(tagModel.getName());

        if (!tag.getName().equalsIgnoreCase(name)) {
            checkTagNameIsFree(name);
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
    public UploadedFileModel addTag(Long fileId, String name) {
        String tagName = TagNameValidator.getValidTagName(name);
        UploadedFile file = findFileByIdOrThrow(fileId);
        Tag tag = findOrCreateTag(tagName);

        file.getTags().add(tag);
        UploadedFile savedFile = uploadedFileRepository.save(file);
        return uploadedFileModelMapper.toModel(savedFile);
    }

    @Override
    public void removeTagFromFile(Long fileId, Long tagId) {
        UploadedFile file = findFileByIdOrThrow(fileId);
        findTagByIdOrThrow(tagId);

        boolean removed = file.getTags().removeIf(fileTag -> fileTag.getId().equals(tagId));

        if (!removed) {
            throw new ResourceNotFoundException("tag is not assigned to file");
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

    private Tag findTagByIdOrThrow(Long id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("tag not found"));
    }

    private void checkTagNameIsFree(String name) {
        if (tagRepository.existsByNameIgnoreCase(name)) {
            throw new ResourceAlreadyExistsException("tag already exists");
        }
    }

    private Tag findOrCreateTag(String name) {
        Tag tag = tagRepository.findByNameIgnoreCase(name).orElse(null);

        if (tag == null) {
            tag = new Tag();
            tag.setName(name);
            tag = tagRepository.save(tag);
        }

        return tag;
    }

    private UploadedFile findFileByIdOrThrow(Long id) {
        return uploadedFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("file not found"));
    }
}
