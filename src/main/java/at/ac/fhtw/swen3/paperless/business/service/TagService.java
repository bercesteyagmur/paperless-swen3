package at.ac.fhtw.swen3.paperless.business.service;

import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;

import java.util.List;

public interface TagService {

    TagModel createTag(TagModel tagModel);

    List<TagModel> getAllTags();

    TagModel getTagById(Long id);

    TagModel updateTag(Long id, TagModel tagModel);

    void deleteTag(Long id);

    UploadedFileModel addTag(Long fileId, String name);

    void removeTagFromFile(Long fileId, Long tagId);

    List<TagModel> getTagsForFile(Long fileId);

    List<UploadedFileModel> getFilesForTag(Long tagId);
}
