package codeArena.repository;

import codeArena.model.Submissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionsRepository extends JpaRepository<Submissions,String> {
    @Query("Select s from Submissions s where s.userId = :userId AND s.problemId = :problemId" +
            "AND s.status='ACCEPTED' ORDER BY s.submittedAt DESC")
    Optional<Submissions> findAcceptedSubmission(@Param("userId") String userId,@Param("problemId") String problemId);

    @Query("SELECT COUNT(s) FROM Submissions S WHERE s.user.userId =:userId")
    long countByUserId(@Param("userId") String userId);

}
