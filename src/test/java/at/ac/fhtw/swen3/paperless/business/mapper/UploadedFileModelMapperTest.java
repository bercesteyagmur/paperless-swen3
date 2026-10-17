package at.ac.fhtw.swen3.paperless.business.mapper;

import at.ac.fhtw.swen3.paperless.business.model.TagModel;
import at.ac.fhtw.swen3.paperless.business.model.UploadedFileModel;
import at.ac.fhtw.swen3.paperless.dal.entity.Tag;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {UploadedFileModelMapperImpl.class, TagModelMapperImpl.class})
class UploadedFileModelMapperTest {

    @Autowired
    private UploadedFileModelMapper mapper;

    @Test
    void mapsFileAndTagToDalEntity() {
        LocalDateTime uploadedAt = LocalDateTime.now();
        TagModel tag = TagModel.builder().id(2L).name("Invoice").build();
        UploadedFileModel model = UploadedFileModel.builder()
                .id(1L)
                .originalFileName("invoice.pdf")
                .fileType("application/pdf")
                .uploadedAt(uploadedAt)
                .tags(Set.of(tag))
                .build();

        UploadedFile entity = mapper.toEntity(model);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getOriginalFileName()).isEqualTo("invoice.pdf");
        assertThat(entity.getFileType()).isEqualTo("application/pdf");
        assertThat(entity.getUploadedAt()).isEqualTo(uploadedAt);
        assertThat(entity.getTags()).hasSize(1);

        Tag mappedTag = entity.getTags().iterator().next();
        assertThat(mappedTag.getId()).isEqualTo(2L);
        assertThat(mappedTag.getName()).isEqualTo("Invoice");
    }
}
