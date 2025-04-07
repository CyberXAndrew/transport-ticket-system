package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.authentication.AuthRequest;
import com.github.cyberxandrew.dto.authentication.AuthResponse;
import com.github.cyberxandrew.dto.authentication.RefreshTokenRequest;
import com.github.cyberxandrew.exception.user.UserNotFoundException;
import com.github.cyberxandrew.model.RefreshToken;
import com.github.cyberxandrew.model.User;
import com.github.cyberxandrew.model.UserDetailsImpl;
import com.github.cyberxandrew.repository.RefreshTokenRepositoryImpl;
import com.github.cyberxandrew.repository.UserRepositoryImpl;
import com.github.cyberxandrew.security.JwtTokenUtil;
import com.github.cyberxandrew.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    @Autowired private RefreshTokenRepositoryImpl refreshTokenRepository;
    @Autowired private UserRepositoryImpl userRepository;

    @PostMapping(path = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) {
        String login = authRequest.getLogin();
        String password = authRequest.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password));

            if (!(authentication.getPrincipal() instanceof UserDetailsImpl userDetails))
                return ResponseEntity.internalServerError().body("Principal is not UserDetailsImpl class");

            Long userId = userDetails.getId();

            final String accessToken = jwtTokenUtil.generateToken(userDetails);

            RefreshToken refreshToken = createRefreshToken(userId);
            refreshTokenRepository.save(refreshToken);

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body("Wrong credentials");
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(500).body("Authentication failed");
        }
    }

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshRequest) throws AuthenticationException {
        String refreshToken = refreshRequest.getRefreshToken();
        return refreshTokenRepository.findByToken(refreshToken)
                .map(refreshTokenEntity -> {
                    if (refreshTokenEntity.getExpiry().isBefore(Instant.now())) {
                        refreshTokenRepository.delete(refreshTokenEntity.getId());
                        return ResponseEntity.status(401).body("Refresh token expired");
                    }

                    User user = userRepository.findById(refreshTokenEntity.getUserId())
                            .orElseThrow(() -> new UserNotFoundException("User not found"));

                    UserDetailsImpl userDetails = new UserDetailsImpl(user.getId(), user.getLogin(),
                            user.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole())));

                    String newAccessToken = jwtTokenUtil.generateToken(userDetails);

                    RefreshToken newRefreshTokenEntity = createRefreshToken(user.getId());
                    refreshTokenRepository.save(newRefreshTokenEntity);
                    refreshTokenRepository.delete(refreshTokenEntity.getId());

                    return ResponseEntity.ok(new AuthResponse(newAccessToken, newRefreshTokenEntity.getToken()));
                })
                .orElseGet(() -> ResponseEntity.status(401).body("Invalid refresh token"));
    }

    private RefreshToken createRefreshToken(Long userId) {
        String refreshToken = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plus(30, ChronoUnit.DAYS);

        return new RefreshToken(userId, refreshToken, expiry);
    }
}
