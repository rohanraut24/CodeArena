package codeArena.repository;

import codeArena.model.Problems;
import codeArena.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagsRepository extends JpaRepository<Tags,String>{

}
