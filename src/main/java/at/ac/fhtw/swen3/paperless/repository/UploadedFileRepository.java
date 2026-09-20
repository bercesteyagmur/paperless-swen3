package at.ac.fhtw.swen3.paperless.repository;

import at.ac.fhtw.swen3.paperless.entity.UploadedFile; // the entity type this repository manages
import org.springframework.data.jpa.repository.JpaRepository; // gives us save(), findAll(), findById(), deleteById(), existsById() for free
import org.springframework.stereotype.Repository; // marks this as a Spring-managed DAL component, enables exception translation

/**
 * This interface is our Data Access Layer for uploaded files
 *  the service layer, controllers should never write raw SQL or think about how data is stored.
 *  It should just call simple methods like save(), findById(), findAll().
 */
@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    // JpaRepository already gives us save(file), findById(id), findAll(), deleteById(id)...
}
