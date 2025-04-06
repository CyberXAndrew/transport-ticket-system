package com.github.cyberxandrew.controller;

import com.github.cyberxandrew.dto.authentication.AuthRequest;
import com.github.cyberxandrew.dto.authentication.AuthResponse;
import com.github.cyberxandrew.dto.authentication.RefreshTokenRequest;
import com.github.cyberxandrew.model.RefreshToken;
import com.github.cyberxandrew.model.UserDetailsImpl;
import com.github.cyberxandrew.repository.RefreshTokenRepositoryImpl;
import com.github.cyberxandrew.security.JwtTokenUtil;
import com.github.cyberxandrew.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationManager authenticationManager;
    @Autowired private RefreshTokenRepositoryImpl refreshTokenRepository;

    @PostMapping(path = "/authenticate")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
        String login = authRequest.getLogin();
        String password = authRequest.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(login, password));

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            final String accessToken = jwtTokenUtil.generateToken(userDetails);
            String refreshToken = UUID.randomUUID().toString();
            Instant expiry = Instant.now().plus(30, ChronoUnit.DAYS);

            RefreshToken refreshTokenEntity = new RefreshToken(userId, refreshToken, expiry);
            refreshTokenRepository.save(refreshTokenEntity);

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (Exception ex) {
            return ResponseEntity.status(401).body("Wrong credentials");
        }
    }

    @PostMapping(path = "/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest refreshRequest) throws Exception {
        return ResponseEntity.ok("Refresh token endpoint");
    }
}
