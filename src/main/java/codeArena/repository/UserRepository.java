package codeArena.repository;

import codeArena.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.BitSet;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<Users, String> {

    Optional<Users> findByEmail(String email);

    Optional<Users> findByGoogleId(String googleId);

    Optional<Users> findByGithubId(String githubId);

    boolean existsByEmail(String email);
}