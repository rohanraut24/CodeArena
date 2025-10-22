package codeArena.model;

import codeArena.model.Problems;
import codeArena.model.RefreshToken;
import codeArena.model.Submissions;
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
@Table(name = "app_users", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_google_id", columnList = "google_id"),
        @Index(name = "idx_github_id", columnList = "github_id")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.GUEST;

    @Column(name = "profile_pic_url")
    private String profilePicUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "github_id", unique = true)
    private String githubId;

    @Column(name = "email_verified")
    private boolean emailVerified = false;

    @Column(name = "account_locked")
    private boolean accountLocked = false;

    @Column(name = "account_enabled")
    private boolean accountEnabled = true;

    // Manual getters for boolean fields (if Lombok doesn't work)
    public boolean isEmailVerified() {
        return emailVerified;
    }

    public boolean isAccountLocked() {
        return accountLocked;
    }

    public boolean isAccountEnabled() {
        return accountEnabled;
    }

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Submissions> submissions = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Problems> createdProblems = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserStatistics statistics;

    public enum Role {
        GUEST, USER, ADMIN
    }

    public enum AuthProvider {
        LOCAL, GOOGLE, GITHUB
    }
}