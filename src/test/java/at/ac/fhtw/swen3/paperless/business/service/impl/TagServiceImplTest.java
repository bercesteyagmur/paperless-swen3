package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.business.mapper.TagModelMapper;
import at.ac.fhtw.swen3.paperless.business.mapper.UploadedFileModelMapper;
import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dal.entity.Tag;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import at.ac.fhtw.swen3.paperless.dal.repository.TagRepository;
import at.ac.fhtw.swen3.paperless.dal.repository.UploadedFileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private TagModelMapper tagModelMapper;

    @Mock
    private UploadedFileRepository uploadedFileRepository;

    @Mock
    private UploadedFileModelMapper uploadedFileModelMapper;

    @InjectMocks
    private TagServiceImpl service;

    @Test
    void createTagTrimsNameAndSaves() {
        TagModel input = tagModel(null, "  Important  ");
        Tag entity = tag(null, "Important");
        Tag savedEntity = tag(1L, "Important");
        TagModel savedModel = tagModel(1L, "Important");

        when(tagRepository.existsByNameIgnoreCase("Important")).thenReturn(false);
        when(tagModelMapper.toEntity(input)).thenReturn(entity);
        when(tagRepository.save(entity)).thenReturn(savedEntity);
        when(tagModelMapper.toModel(savedEntity)).thenReturn(savedModel);

        TagModel result = service.createTag(input);

        assertThat(result).isSameAs(savedModel);
        assertThat(input.getName()).isEqualTo("Important");

        verify(tagRepository).save(entity);
    }

    @Test
    void createTagRejectsBlankName() {
        TagModel input = tagModel(null, "   ");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createTag(input)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void createTagRejectsDuplicateName() {
        TagModel input = tagModel(null, "Important");
        when(tagRepository.existsByNameIgnoreCase("Important")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.createTag(input)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void updateTagRejectsDuplicateName() {
        Tag storedTag = tag(1L, "Invoice");
        TagModel input = tagModel(null, "Important");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(storedTag));
        when(tagRepository.existsByNameIgnoreCase("Important")).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.updateTag(1L, input)
        );

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(storedTag.getName()).isEqualTo("Invoice");
        verify(tagRepository, never()).save(any());
    }

    @Test
    void addTagUsesExistingTag() {
        UploadedFile file = file(1L, "invoice.pdf");
        Tag tag = tag(2L, "Invoice");

        when(uploadedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(tagRepository.findByNameIgnoreCase("Invoice")).thenReturn(Optional.of(tag));

        service.addTag(1L, "Invoice");

        assertThat(file.getTags()).containsExactly(tag);
        verify(tagRepository, never()).save(any());
    }

    @Test
    void addTagCreatesNewTag() {
        UploadedFile file = file(1L, "invoice.pdf");
        Tag savedTag = tag(3L, "October");

        when(uploadedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(tagRepository.findByNameIgnoreCase("October")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);

        service.addTag(1L, "October");

        assertThat(file.getTags()).containsExactly(savedTag);
    }

    @Test
    void removeTagRemovesTagFromFile() {
        Tag tag = tag(2L, "Invoice");
        UploadedFile file = file(1L, "invoice.pdf");
        file.getTags().add(tag);

        when(uploadedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));

        service.removeTagFromFile(1L, 2L);

        assertThat(file.getTags()).isEmpty();
        verify(uploadedFileRepository).save(file);
    }

    @Test
    void deleteTagRemovesRelationshipsAndKeepsFiles() {
        Tag tag = tag(2L, "Invoice");
        UploadedFile firstFile = file(1L, "first.pdf");
        UploadedFile secondFile = file(2L, "second.pdf");
        firstFile.getTags().add(tag);
        secondFile.getTags().add(tag);

        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));
        when(uploadedFileRepository.findAllByTags_Id(2L)).thenReturn(List.of(firstFile, secondFile));

        service.deleteTag(2L);

        assertThat(firstFile.getTags()).isEmpty();
        assertThat(secondFile.getTags()).isEmpty();
        verify(uploadedFileRepository).saveAll(List.of(firstFile, secondFile));
        verify(tagRepository).delete(tag);
        verify(uploadedFileRepository, never()).delete(any());
    }

    @Test
    void getTagsForFileReturnsMappedTags() {
        Tag invoiceTag = tag(2L, "Invoice");
        Tag importantTag = tag(3L, "Important");
        UploadedFile file = file(1L, "invoice.pdf");
        TagModel invoiceModel = tagModel(2L, "Invoice");
        TagModel importantModel = tagModel(3L, "Important");
        file.getTags().add(invoiceTag);
        file.getTags().add(importantTag);

        when(uploadedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(tagModelMapper.toModel(invoiceTag)).thenReturn(invoiceModel);
        when(tagModelMapper.toModel(importantTag)).thenReturn(importantModel);

        List<TagModel> result = service.getTagsForFile(1L);

        assertThat(result).containsExactlyInAnyOrder(invoiceModel, importantModel);
    }

    @Test
    void getFilesForTagReturnsMappedFiles() {
        Tag tag = tag(2L, "Invoice");
        UploadedFile firstFile = file(1L, "first.pdf");
        UploadedFile secondFile = file(2L, "second.pdf");
        UploadedFileModel firstModel = fileModel(1L, "first.pdf");
        UploadedFileModel secondModel = fileModel(2L, "second.pdf");

        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));
        when(uploadedFileRepository.findAllByTags_Id(2L)).thenReturn(List.of(firstFile, secondFile));
        when(uploadedFileModelMapper.toModel(firstFile)).thenReturn(firstModel);
        when(uploadedFileModelMapper.toModel(secondFile)).thenReturn(secondModel);

        List<UploadedFileModel> result = service.getFilesForTag(2L);

        assertThat(result).containsExactly(firstModel, secondModel);
    }

    private static Tag tag(Long id, String name) {
        return Tag.builder()
                .id(id)
                .name(name)
                .build();
    }

    private static TagModel tagModel(Long id, String name) {
        return TagModel.builder()
                .id(id)
                .name(name)
                .build();
    }

    private static UploadedFile file(Long id, String name) {
        return UploadedFile.builder()
                .id(id)
                .originalFileName(name)
                .fileType("application/pdf")
                .build();
    }

    private static UploadedFileModel fileModel(Long id, String name) {
        return UploadedFileModel.builder()
                .id(id)
                .originalFileName(name)
                .fileType("application/pdf")
                .build();
    }
}
