package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.mapper.TagModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.business.service.TagService;
import at.ac.fhtw.swen3.paperless.dal.entity.Tag;
import at.ac.fhtw.swen3.paperless.dal.repository.TagRepository;

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

    @Override
    public TagModel createTag(TagModel tagModel) {

        if (tagModel == null || tagModel.getName() == null || tagModel.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tag name must not be empty");
        }

        String name = tagModel.getName().trim();
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
}
