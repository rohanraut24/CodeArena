package codeArena.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "test_cases", indexes = {
//        @Index(name = "idx_problem_id", columnList = "problem_id"),
//        @Index(name = "idx_is_sample", columnList = "is_sample")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCases {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "test_case_id")
    private String testCaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false, foreignKey = @ForeignKey(name = "fk_testcase_problem"))
    private Problems problem;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String input;

    @Column(name = "expected_output", columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    @Column(name = "is_sample")
    private boolean isSample = false;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}