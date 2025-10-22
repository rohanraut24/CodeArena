package codeArena.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "problems", indexes = {
        @Index(name = "idx_difficulty", columnList = "difficulty"),
        @Index(name = "idx_created_by", columnList = "created_by"),
        @Index(name = "idx_is_daily", columnList = "is_daily_problem")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Problems {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "problem_id")
    private String problemId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", foreignKey = @ForeignKey(name = "fk_problem_creator"))
    private Users createdBy;

    @Column(name = "is_daily_problem")
    private boolean isDailyProblem = false;

    @Column(name = "version")
    private int version = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // One Problem has many Test Cases
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestCases> testCases = new ArrayList<>();

    // One Problem has many Submissions
    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Submissions> submissions = new ArrayList<>();

    // One Problem belongs to many Tags (Many-to-Many)
    @ManyToMany
    @JoinTable(
            name = "problem_tags",
            joinColumns = @JoinColumn(name = "problem_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tags> tags = new ArrayList<>();

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }

    // Helper methods
    public void addTestCase(TestCases testCase) {
        testCases.add(testCase);
        testCase.setProblem(this);
    }

    public void removeTestCase(TestCases testCase) {
        testCases.remove(testCase);
        testCase.setProblem(null);
    }

    public void addTag(Tags tag) {
        tags.add(tag);
        tag.getProblems().add(this);
    }

    public void removeTag(Tags tag) {
        tags.remove(tag);
        tag.getProblems().remove(this);
    }
}