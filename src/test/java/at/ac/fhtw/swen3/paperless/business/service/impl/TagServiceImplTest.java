package at.ac.fhtw.swen3.paperless.business.service.impl;

import at.ac.fhtw.swen3.paperless.exception.InvalidInputException;
import at.ac.fhtw.swen3.paperless.exception.ResourceAlreadyExistsException;
import at.ac.fhtw.swen3.paperless.exception.ResourceNotFoundException;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Important");
        assertThat(input.getName()).isEqualTo("Important");

        verify(tagRepository).save(entity);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" "})
    void createTagRejectsBlankName(String tagName) {
        TagModel input = tagModel(null, tagName);

        assertThrows(
                InvalidInputException.class,
                () -> service.createTag(input)
        );

        verify(tagRepository, never()).save(any());
    }

    @Test
    void createTagRejectsLongName() {
        TagModel input = tagModel(null, "a".repeat(51));

        assertThrows(
                InvalidInputException.class,
                () -> service.createTag(input)
        );

        verify(tagRepository, never()).save(any());
    }

    @Test
    void createTagRejectsDuplicateName() {
        TagModel input = tagModel(null, "Important");
        when(tagRepository.existsByNameIgnoreCase("Important")).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.createTag(input)
        );

        verify(tagRepository, never()).save(any());
    }

    @Test
    void updateTagRejectsDuplicateName() {
        Tag storedTag = tag(1L, "Invoice");
        TagModel input = tagModel(null, "Important");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(storedTag));
        when(tagRepository.existsByNameIgnoreCase("Important")).thenReturn(true);

        assertThrows(
                ResourceAlreadyExistsException.class,
                () -> service.updateTag(1L, input)
        );

        assertThat(storedTag.getName()).isEqualTo("Invoice");
        verify(tagRepository, never()).save(any());
    }

    @Test
    void updateTagRenamesAndSaves() {
        Tag storedTag = tag(1L, "Invoice");
        TagModel input = tagModel(null, "Important");
        TagModel updatedTag = tagModel(1L, "Important");

        when(tagRepository.findById(1L)).thenReturn(Optional.of(storedTag));
        when(tagRepository.save(storedTag)).thenReturn(storedTag);
        when(tagModelMapper.toModel(storedTag)).thenReturn(updatedTag);

        TagModel result = service.updateTag(1L, input);

        assertThat(storedTag.getName()).isEqualTo("Important");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Important");
        verify(tagRepository).save(storedTag);
    }

    @Test
    void addTagCreatesTagAndSavesFile() {
        UploadedFile file = file(1L, "invoice.pdf");
        Tag savedTag = tag(2L, "Invoice");
        UploadedFileModel updatedFile = fileModel(1L, "invoice.pdf");

        when(uploadedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(tagRepository.findByNameIgnoreCase("Invoice")).thenReturn(Optional.empty());
        when(tagRepository.save(any(Tag.class))).thenReturn(savedTag);
        when(uploadedFileRepository.save(file)).thenReturn(file);
        when(uploadedFileModelMapper.toModel(file)).thenReturn(updatedFile);

        UploadedFileModel result = service.addTag(1L, "Invoice");

        assertThat(file.getTags()).containsExactly(savedTag);
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getOriginalFileName()).isEqualTo("invoice.pdf");
        verify(tagRepository).save(any(Tag.class));
        verify(uploadedFileRepository).save(file);
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
    void removeTagRejectsTagThatIsNotAssigned() {
        UploadedFile file = file(1L, "invoice.pdf");
        Tag tag = tag(2L, "Invoice");

        when(uploadedFileRepository.findById(1L)).thenReturn(Optional.of(file));
        when(tagRepository.findById(2L)).thenReturn(Optional.of(tag));

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.removeTagFromFile(1L, 2L)
        );

        verify(uploadedFileRepository, never()).save(any());
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
