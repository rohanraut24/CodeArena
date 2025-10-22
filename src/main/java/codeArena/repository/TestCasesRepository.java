package codeArena.repository;

import codeArena.model.RefreshToken;
import codeArena.model.TestCases;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestCasesRepository extends JpaRepository<TestCases, String> {

}


