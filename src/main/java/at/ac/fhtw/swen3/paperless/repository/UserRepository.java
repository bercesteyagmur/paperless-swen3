package at.ac.fhtw.swen3.paperless.repository;

import at.ac.fhtw.swen3.paperless.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
