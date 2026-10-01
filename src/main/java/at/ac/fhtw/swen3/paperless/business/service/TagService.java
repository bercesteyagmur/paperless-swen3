package at.ac.fhtw.swen3.paperless.business.service;

import at.ac.fhtw.swen3.paperless.business.model.TagModel;

import java.util.List;

public interface TagService {

    TagModel createTag(TagModel tagModel);

    List<TagModel> getAllTags();
}
