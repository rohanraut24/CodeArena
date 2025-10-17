package codeArena.model;

import codeArena.model.Submissions;
import codeArena.model.TestCases;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "submission_test_cases", indexes = {
        @Index(name = "idx_submission_id", columnList = "submission_id"),
        @Index(name = "idx_test_case_id", columnList = "test_case_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionTestCases {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sub_testcase_submission"))
    private Submissions submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false, foreignKey = @ForeignKey(name = "fk_sub_testcase_testcase"))
    private TestCases testCase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "execution_time")
    private Float executionTime;

    @Column(name = "memory_usage")
    private Float memoryUsage;

    @Column(columnDefinition = "TEXT")
    private String output;

    @Column(columnDefinition = "TEXT")
    private String error;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Status {
        PASSED,
        FAILED,
        ERROR,
        TIMEOUT
    }
}