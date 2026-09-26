package at.ac.fhtw.swen3.paperless.repository;

import at.ac.fhtw.swen3.paperless.entity.UploadedFile;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

// Starts only the database part of Spring and uses H2 for the tests
@DataJpaTest
class UploadedFileRepositoryTest {

    @Autowired
    private UploadedFileRepository uploadedFileRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void saveStoresFile() {
        UploadedFile file = createFile("report.pdf");

        // Save the file directly to H2 so it gets an id
        UploadedFile savedFile = uploadedFileRepository.saveAndFlush(file);

        assertThat(savedFile.getId()).isNotNull();
        assertThat(savedFile.getOriginalFileName()).isEqualTo("report.pdf");
        assertThat(savedFile.getFileType()).isEqualTo("application/pdf");
        assertThat(savedFile.getUploadedAt()).isNotNull();
    }

    @Test
    void findByIdReturnsStoredFile() {
        // Save a file first so we can search for its generated id
        UploadedFile savedFile = uploadedFileRepository.saveAndFlush(createFile("report.pdf"));

        var result = uploadedFileRepository.findById(savedFile.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getOriginalFileName()).isEqualTo("report.pdf");
    }

    @Test
    void findByIdReturnsEmptyForUnknownFile() {
        // This id was never saved so the result should be empty
        var result = uploadedFileRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllReturnsStoredFiles() {
        // Add two files to check if both are returned
        uploadedFileRepository.save(createFile("first.pdf"));
        uploadedFileRepository.save(createFile("second.pdf"));

        var result = uploadedFileRepository.findAll();

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

        // Clear the cache so the file is really loaded from H2 again
        entityManager.clear();

        UploadedFile updatedFile = uploadedFileRepository.findById(savedFile.getId()).orElseThrow();
        assertThat(updatedFile.getOriginalFileName()).isEqualTo("new-name.pdf");
    }

    @Test
    void deleteRemovesStoredFile() {
        UploadedFile savedFile = uploadedFileRepository.saveAndFlush(createFile("report.pdf"));

        uploadedFileRepository.deleteById(savedFile.getId());

        // Flush makes sure the delete is sent to H2 before checking
        uploadedFileRepository.flush();

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
