package codeArena.security;

import codeArena.model.RefreshToken;
import codeArena.model.Users;
import codeArena.repository.UserRepository;
import codeArena.service.JwtService;
import codeArena.service.RefreshTokenService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = oAuth2Token.getPrincipal();
        String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();

        Users users = processOAuth2User(oAuth2User, registrationId);

        String accessToken = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        users.getEmail(),
                        "",
                        java.util.Collections.singletonList(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + users.getRole())
                        )
                )
        );

        RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(users.getUserId(), request);
        String refreshToken = refreshTokenEntity.getToken();

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private Users processOAuth2User(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String email;
        String name;
        String profilePicUrl;
        String providerId;

        if ("google".equals(provider)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            profilePicUrl = (String) attributes.get("picture");
            providerId = (String) attributes.get("sub");

            final String finalEmail = email;
            final String finalName = name;
            final String finalProfilePicUrl = profilePicUrl;
            final String finalProviderId = providerId;

            return userRepository.findByGoogleId(finalProviderId)
                    .or(() -> userRepository.findByEmail(finalEmail))
                    .map(existingUser -> {
                        if (existingUser.getGoogleId() == null) {
                            existingUser.setGoogleId(finalProviderId);
                            existingUser.setAuthProvider(Users.AuthProvider.GOOGLE);
                        }
                        existingUser.setName(finalName);
                        existingUser.setProfilePicUrl(finalProfilePicUrl);
                        existingUser.setEmailVerified(true);
                        return userRepository.save(existingUser);
                    })
                    .orElseGet(() -> createNewUser(finalEmail, finalName, finalProfilePicUrl, finalProviderId, Users.AuthProvider.GOOGLE));

        } else if ("github".equals(provider)) {
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            if (name == null) {
                name = (String) attributes.get("login");
            }
            profilePicUrl = (String) attributes.get("avatar_url");
            providerId = String.valueOf(attributes.get("id"));

            final String finalEmail = email;
            final String finalName = name;
            final String finalProfilePicUrl = profilePicUrl;
            final String finalProviderId = providerId;

            return userRepository.findByGithubId(finalProviderId)
                    .or(() -> userRepository.findByEmail(finalEmail))
                    .map(existingUser -> {
                        if (existingUser.getGithubId() == null) {
                            existingUser.setGithubId(finalProviderId);
                            existingUser.setAuthProvider(Users.AuthProvider.GITHUB);
                        }
                        existingUser.setName(finalName);
                        existingUser.setProfilePicUrl(finalProfilePicUrl);
                        existingUser.setEmailVerified(true);
                        return userRepository.save(existingUser);
                    })
                    .orElseGet(() -> createNewUser(finalEmail, finalName, finalProfilePicUrl, finalProviderId, Users.AuthProvider.GITHUB));
        }

        throw new RuntimeException("Unsupported OAuth2 provider: " + provider);
    }
    private Users createNewUser(String email, String name, String profilePicUrl,
                                String providerId, Users.AuthProvider provider) {
        Users newUser = Users.builder()
                .email(email)
                .name(name)
                .profilePicUrl(profilePicUrl)
                .authProvider(provider)
                .role(Users.Role.USER)
                .emailVerified(true)
                .accountEnabled(true)
                .accountLocked(false)
                .build();

        if (provider == Users.AuthProvider.GOOGLE) {
            newUser.setGoogleId(providerId);
        } else if (provider == Users.AuthProvider.GITHUB) {
            newUser.setGithubId(providerId);
        }

        return userRepository.save(newUser);
    }
}