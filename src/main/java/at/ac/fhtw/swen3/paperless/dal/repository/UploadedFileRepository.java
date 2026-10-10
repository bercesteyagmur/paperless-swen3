package at.ac.fhtw.swen3.paperless.dal.repository;

import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    List<UploadedFile> findAllByTags_Id(Long tagId);

    boolean existsByOriginalFileNameIgnoreCase(String originalFileName);
}
