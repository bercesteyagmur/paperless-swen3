package at.ac.fhtw.swen3.paperless.dal.repository;

import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UploadedFileRepositoryTest {

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveGeneratesFileId() {
        UploadedFile file = createFile("report.pdf");

        UploadedFile savedFile = uploadedFileRepository.save(file);

        assertThat(savedFile.getId()).isNotNull();
    }

    @Test
    void findByIdReturnsStoredFile() {
        UploadedFile savedFile = uploadedFileRepository.save(createFile("report.pdf"));

        Optional<UploadedFile> result = uploadedFileRepository.findById(savedFile.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getOriginalFileName()).isEqualTo("report.pdf");
    }

    @Test
    void findByIdReturnsEmptyForUnknownFile() {
        Optional<UploadedFile> result = uploadedFileRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllReturnsStoredFiles() {
        uploadedFileRepository.save(createFile("first.pdf"));
        uploadedFileRepository.save(createFile("second.pdf"));

        List<UploadedFile> result = uploadedFileRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(UploadedFile::getOriginalFileName)
                .containsExactlyInAnyOrder("first.pdf", "second.pdf");
    }

    @Test
    void updateChangesStoredFileName() {
        UploadedFile savedFile = uploadedFileRepository.saveAndFlush(createFile("old-name.pdf"));

        savedFile.setOriginalFileName("new-name.pdf");
        uploadedFileRepository.saveAndFlush(savedFile);

        entityManager.clear();

        UploadedFile updatedFile = uploadedFileRepository.findById(savedFile.getId()).orElseThrow();
        assertThat(updatedFile.getOriginalFileName()).isEqualTo("new-name.pdf");
    }

    @Test
    void deleteRemovesStoredFile() {
        UploadedFile savedFile = uploadedFileRepository.saveAndFlush(createFile("report.pdf"));

        uploadedFileRepository.deleteById(savedFile.getId());

        assertThat(uploadedFileRepository.findById(savedFile.getId())).isEmpty();
    }

    private UploadedFile createFile(String fileName) {
        return UploadedFile.builder()
                .originalFileName(fileName)
                .fileType("application/pdf")
                .uploadedAt(LocalDateTime.now())
                .build();
    }
}
