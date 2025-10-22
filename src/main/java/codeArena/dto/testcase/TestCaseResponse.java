package codeArena.dto.testcase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseResponse {
    private String testCaseId;
    private String input;
    private String exceptionOutput;
    private boolean isSample;
    private String explanation;
}
