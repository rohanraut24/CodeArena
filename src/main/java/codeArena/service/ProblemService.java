package codeArena.service;

import codeArena.dto.problem.ProblemsListResponse;
import codeArena.model.Problems;
import codeArena.repository.ProblemsRepository;
import codeArena.repository.SubmissionsRepository;
import codeArena.repository.TagsRepository;
import codeArena.repository.TestCasesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {
    private final ProblemsRepository problemsRepository;
    private final TestCasesRepository testCasesRepository;
    private final SubmissionsRepository submissionsRepository;
    private final TagsRepository tagsRepository;
    public final

    List<ProblemsListResponse> getAllProblems(String userId){
        List<Problems> problems = problemsRepository.findAll();

        return problems.stream().map(problem->{
            boolean solved =false;
            int submissionCount =0;

            if (userId != null) {
                solved = submissionsRepository.findAcceptedSubmission(userId, problem.getProblemId()).isPresent();
                submissionCount = (int) submissionsRepository.countByUserId(userId);
            }
            return ProblemsListResponse.builder()
                    .problemId(problem.getProblemId())
                    .title(problem.getTitle())
                    .tags(problem.getTags())
                    .difficulty(problem.getDifficulty())
                    .solved(solved)
                    .submissionCount(submissionCount)
                    .build();
        }).collect(Collectors.toList());
    }


}
