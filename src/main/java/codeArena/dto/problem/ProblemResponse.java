package codeArena.dto.problem;

import codeArena.dto.testcase.TestCaseResponse;
import codeArena.model.Tags;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProblemResponse {
    private String problemId;
    private String title;
    private String decsription;
    private String difficulty;
    private boolean isDailyProblem;
    private int version;
    private String createdBy;
    private List<Tags> tags;
    private List<TestCaseResponse> sampleTestCases;
    private LocalDateTime createAt;
    private LocalDateTime updatedAt;
}
