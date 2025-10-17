package codeArena.service;

import codeArena.dto.*;
import codeArena.exception.DuplicateResourceException;
import codeArena.exception.InvalidCredentialsException;
import codeArena.model.RefreshToken;
import codeArena.model.Users;
import codeArena.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered");
        }

        Users users = Users.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Users.Role.USER)
                .authProvider(Users.AuthProvider.LOCAL)
                .emailVerified(false)
                .accountEnabled(true)
                .accountLocked(false)
                .build();

        users = userRepository.save(users);

        UserDetails userDetails = userDetailsService.loadUserByUsername(users.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(users.getUserId(), httpRequest);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(3600000L) // 1 hour in milliseconds
                .users(mapToUserResponse(users))
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        Users user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateToken(userDetails);
        codeArena.model.RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUserId(), httpRequest);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(3600000L)
                .users(mapToUserResponse(user))
                .build();
    }

    @Transactional
    public RefreshTokenResponse  refreshToken(RefreshTokenRequest request) {
        codeArena.model.RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyExpiration(refreshToken);

        Users user = refreshToken.getUser();
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String newAccessToken = jwtService.generateToken(userDetails);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(3600000L)
                .build();
    }

    @Transactional
    public MessageResponse logout(String authHeader, RefreshTokenRequest refreshTokenRequest) {
        if (refreshTokenRequest != null && refreshTokenRequest.getRefreshToken() != null) {
            refreshTokenService.revokeToken(refreshTokenRequest.getRefreshToken());
        }
        return new MessageResponse("Logged out successfully");
    }

    public UserResponse getCurrentUser(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);

        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

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

    @Service
    @RequiredArgsConstructor
    public static class CustomUserDetailsService implements UserDetailsService {

        private final UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            Users users = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            return new org.springframework.security.core.userdetails.User(
                    users.getEmail(),
                    users.getPassword() != null ? users.getPassword() : "",
                    users.getAccountEnabled(),
                    true,
                    true,
                    !users.getAccountLocked(),
                    getAuthorities(users)
            );
        }

        private Collection<? extends GrantedAuthority> getAuthorities(Users users) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + users.getRole().name()));
        }
    }
}