package at.ac.fhtw.swen3.paperless.dal.repository;

import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
}
