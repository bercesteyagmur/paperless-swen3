package at.ac.fhtw.swen3.paperless.dal.repository;

import at.ac.fhtw.swen3.paperless.dal.entity.Tag;
import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Test
    void findsTagNameIgnoringCase() {
        tagRepository.save(Tag.builder().name("Important").build());

        assertThat(tagRepository.existsByNameIgnoreCase("important")).isTrue();
        assertThat(tagRepository.existsByNameIgnoreCase("private")).isFalse();
    }

    @Test
    void findsOnlyFilesWithSelectedTag() {
        Tag invoiceTag = tagRepository.saveAndFlush(Tag.builder().name("Invoice").build());
        UploadedFile invoice = createFile("invoice.pdf");
        invoice.getTags().add(invoiceTag);

        UploadedFile savedInvoice = uploadedFileRepository.saveAndFlush(invoice);
        uploadedFileRepository.saveAndFlush(createFile("notes.pdf"));

        var result = uploadedFileRepository.findAllByTags_Id(invoiceTag.getId());

        assertThat(result)
                .extracting(UploadedFile::getId)
                .containsExactly(savedInvoice.getId());
    }

    private UploadedFile createFile(String name) {
        return UploadedFile.builder()
                .originalFileName(name)
                .fileType("application/pdf")
                .uploadedAt(LocalDateTime.now())
                .build();
    }
}
