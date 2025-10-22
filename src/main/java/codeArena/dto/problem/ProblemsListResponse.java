package codeArena.dto.problem;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemsListResponse {
    private String problemId;
    private String title;
    private String difficulty;
    private boolean isDailyProblem;
    private boolean solved;
    private int submissionCount;
    private List<String> tags;
}
