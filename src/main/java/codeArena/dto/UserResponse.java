package codeArena.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String userId;
    private String name;
    private String email;
    private String role;
    private String profilePicUrl;
    private String authProvider;
    private boolean emailVerified;
    private LocalDateTime createdAt;
}