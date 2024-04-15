package com.app.service.auth;

import com.app.dto.auth.JwtAuthenticationResponse;
import com.app.dto.auth.SignInRequest;
import com.app.dto.auth.SignUpRequest;
import com.app.model.user.Role;
import com.app.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    /**
     * User service.
     */
    private final UserService userService;

    /**
     * Service for token generation and validation.
     */
    private final JwtService jwtService;

    /**
     * Service for encoding password.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Authentication manager.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя.
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(final SignUpRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        userService.create(user);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя.
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(final SignInRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }
}
