package at.ac.fhtw.swen3.paperless.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// One row in the "users" table = one person who can upload files.
// This is the entity for our use-case 4 = every UploadedFile belongs to exactly one User.
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //since id is Long -> UserRepository is JpaRepository<User, Long>
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    // list of all files this user uploaded
    // if we delete the user, all of the files of the user will be deleted
    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL)
    private List<UploadedFile> files;
}
