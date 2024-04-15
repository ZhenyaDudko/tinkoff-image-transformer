package com.app.controller;

import com.app.dto.auth.JwtAuthenticationResponse;
import com.app.dto.auth.SignInRequest;
import com.app.dto.auth.SignUpRequest;
import com.app.service.auth.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public final class AuthController {

    /**
     * Authentication service.
     */
    private final AuthenticationService authenticationService;

    /**
     * Sign up user with given credentials.
     * @param request username and password.
     * @return JWT token.
     */
    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(
            @RequestBody @Valid final SignUpRequest request
    ) {
        return authenticationService.signUp(request);
    }

    /**
     * Sign in user with given credentials.
     * @param request username and password.
     * @return JWT token.
     */
    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(
            @RequestBody @Valid final SignInRequest request
    ) {
        return authenticationService.signIn(request);
    }
}
