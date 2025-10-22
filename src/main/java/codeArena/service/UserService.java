package codeArena.service;

import codeArena.dto.UserResponse;
import codeArena.exception.ResourceNotFoundException;
import codeArena.model.Users;
import codeArena.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse upgradeToUser(String userId) {
        Users users = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (users.getRole() == Users.Role.GUEST) {
            users.setRole(Users.Role.USER);
            users = userRepository.save(users);
        }

        return mapToUserResponse(users);
    }

    @Transactional
    public UserResponse upgradeToAdmin(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(Users.Role.ADMIN);
        user = userRepository.save(user);

        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse downgradeToGuest(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setRole(Users.Role.GUEST);
        user = userRepository.save(user);

        return mapToUserResponse(user);
    }

    public UserResponse getUserById(String userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToUserResponse(user);
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