package codeArena.restController;

import codeArena.dto.problem.ProblemsListResponse;
import codeArena.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final ProblemService problemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GUEST','ADMIN','USER')")
    public ResponseEntity<List<ProblemsListResponse>> getAllProblems(Authentication authentication){
        String email =authentication.getName();

        List<ProblemsListResponse> problem = problemService.getAllProblems(email);
    }
}
