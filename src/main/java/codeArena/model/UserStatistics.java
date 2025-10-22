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
@Table(name = "user_statistics", indexes = {
        @Index(name = "idx_user_stats", columnList = "user_id", unique = true)
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "stats_id")
    private String statsId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_stats_user"))
    private Users user;

    @Column(name = "problems_solved")
    @Builder.Default
    private int problemsSolved = 0;

    @Column(name = "easy_solved")
    @Builder.Default
    private int easySolved = 0;

    @Column(name = "medium_solved")
    @Builder.Default
    private int mediumSolved = 0;

    @Column(name = "hard_solved")
    @Builder.Default
    private int hardSolved = 0;

    @Column(name = "total_submissions")
    @Builder.Default
    private int totalSubmissions = 0;

    @Column(name = "accepted_submissions")
    @Builder.Default
    private int acceptedSubmissions = 0;

    @Column(name = "current_streak")
    @Builder.Default
    private int currentStreak = 0;

    @Column(name = "max_streak")
    @Builder.Default
    private int maxStreak = 0;

    @Column(name = "last_submission_date")
    private LocalDateTime lastSubmissionDate;

    @Column(name = "total_score")
    @Builder.Default
    private int totalScore = 0;

    @Column(name = "ranking")
    private Integer ranking;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}