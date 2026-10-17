package at.ac.fhtw.swen3.paperless.dal.repository;

import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UploadedFileRepositoryTest {

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void fileCanBeSavedRenamedAndDeleted() {
        UploadedFile savedFile = uploadedFileRepository.saveAndFlush(createFile("old-name.pdf"));
        Long fileId = savedFile.getId();

        assertThat(fileId).isNotNull();

        savedFile.setOriginalFileName("new-name.pdf");
        uploadedFileRepository.saveAndFlush(savedFile);
        entityManager.clear();

        UploadedFile renamedFile = uploadedFileRepository.findById(fileId).orElseThrow();
        assertThat(renamedFile.getOriginalFileName()).isEqualTo("new-name.pdf");

        uploadedFileRepository.delete(renamedFile);
        uploadedFileRepository.flush();

        assertThat(uploadedFileRepository.findById(fileId)).isEmpty();
    }

    @Test
    void findAllReturnsStoredFiles() {
        uploadedFileRepository.save(createFile("first.pdf"));
        uploadedFileRepository.save(createFile("second.pdf"));

        List<UploadedFile> result = uploadedFileRepository.findAll();

        assertThat(result)
                .extracting(UploadedFile::getOriginalFileName)
                .containsExactlyInAnyOrder("first.pdf", "second.pdf");
    }

    private UploadedFile createFile(String fileName) {
        return UploadedFile.builder()
                .originalFileName(fileName)
                .fileType("application/pdf")
                .uploadedAt(LocalDateTime.now())
                .build();
    }
}
