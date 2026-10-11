package at.ac.fhtw.swen3.paperless.dal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "uploaded_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String originalFileName;

    @Column(nullable = false)
    private String fileType;

    @Column(nullable = false)
    private LocalDateTime uploadedAt;




    @ManyToMany
    @JoinTable(
            name = "uploaded_file_tags",

            joinColumns = @JoinColumn(name = "uploaded_file_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )

    @Builder.Default
    private Set<Tag> tags = new HashSet<>();
}
