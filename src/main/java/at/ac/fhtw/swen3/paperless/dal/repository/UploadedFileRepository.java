package at.ac.fhtw.swen3.paperless.dal.repository;

import at.ac.fhtw.swen3.paperless.dal.entity.UploadedFile;
import org.springframework.data.jpa.repository.JpaRepository; // gives us save(), findAll(), findById(), deleteById(), existsById()

/**
 * This interface is our Data Access Layer for uploaded files
 *  the service layer, controllers should never write raw SQL or think about how data is stored.
 *  It should just call simple methods like save(), findById(), findAll().
 */
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    // JpaRepository already gives us save(file), findById(id), findAll(), deleteById(id)...
}
