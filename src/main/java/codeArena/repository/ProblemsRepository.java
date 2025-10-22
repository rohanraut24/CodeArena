package codeArena.repository;

import codeArena.model.Problems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemsRepository extends JpaRepository<Problems,String>{

}
