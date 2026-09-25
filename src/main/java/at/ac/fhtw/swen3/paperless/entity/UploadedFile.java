package at.ac.fhtw.swen3.paperless.entity;

import jakarta.persistence.*; // JPA annotations (@Entity, @Id, @Column, ...) - the "rules" for mapping this class to a database table
import lombok.AllArgsConstructor; // generates a constructor that takes every field as a parameter
import lombok.Builder; // generates the builder pattern, e.g. UploadedFile.builder().originalFileName("x").build()
import lombok.Getter; // generates getId(), getOriginalFileName(), etc. automatically
import lombok.NoArgsConstructor; // generates an empty constructor - JPA/Hibernate needs this to build objects via reflection
import lombok.Setter; // generates setId(...), setOriginalFileName(...), etc. automatically

import java.time.LocalDateTime; // the type we use for the uploadedAt timestamp

/**
 * One row in the uploaded_files table = one uploaded file
 * @Entity tells Hibernate "please create a table for this class and map every field to a column".
 */
@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
// JPA needs constructor to build objects
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedFile {

    @Id
    //auto incremented id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // name of the file  on the user's computer
    @Column(nullable = false)
    private String originalFileName;

    // type of the file
    // frontend needs this to know how to handle the file (maybe?)
    @Column(nullable = false)
    private String fileType;

    // Timestamp to show when row was created.
    @Column(nullable = false)
    private LocalDateTime uploadedAt;

}
