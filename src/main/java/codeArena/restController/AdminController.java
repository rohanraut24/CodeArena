package codeArena.restController;

import codeArena.dto.UserResponse;
import codeArena.model.Users;
import codeArena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<Users  > users = userRepository.findAll();

        List<UserResponse> userResponses = users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(mapToUserResponse(users));
    }

    @PutMapping("/users/{userId}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable String userId,
            @RequestParam String role) {

        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        users.setRole(Users.Role.valueOf(role));
        users = userRepository.save(users);

        return ResponseEntity.ok(mapToUserResponse(users));
    }

    @PutMapping("/users/{userId}/lock")
    public ResponseEntity<UserResponse> lockUser(@PathVariable String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountLocked(true);
        user = userRepository.save(user);

        return ResponseEntity.ok(mapToUserResponse(user));
    }

    @PutMapping("/users/{userId}/unlock")
    public ResponseEntity<UserResponse> unlockUser(@PathVariable String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setAccountLocked(false);
        user = userRepository.save(user);

        return ResponseEntity.ok(mapToUserResponse(user));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    private UserResponse mapToUserResponse(Users user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .profilePicUrl(user.getProfilePicUrl())
                .authProvider(user.getAuthProvider().name())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}