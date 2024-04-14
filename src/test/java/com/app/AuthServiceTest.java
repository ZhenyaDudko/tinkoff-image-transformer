package com.app;

import com.app.dto.auth.SignInRequest;
import com.app.dto.auth.SignUpRequest;
import com.app.model.user.Role;
import com.app.repository.UserRepository;
import com.app.service.auth.AuthenticationService;
import com.app.service.auth.JwtService;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class AuthServiceTest extends AbstractTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    @AfterEach
    public void clear() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @Transactional
    @SneakyThrows
    public void registrationTest() {
        var signUpRequest = new SignUpRequest("user", "123");
        var signInRequest = new SignInRequest("user", "123");

        assertThrows(BadCredentialsException.class, () -> authService.signIn(signInRequest));

        var jwtToken = authService.signUp(signUpRequest).getToken();

        var savedUser = userRepository.findByUsername("user").orElseThrow();

        assertEquals(signUpRequest.getUsername(), savedUser.getUsername());
        assertEquals(Role.ROLE_USER, savedUser.getRole());

        assertTrue(jwtService.isTokenValid(jwtToken, savedUser));

        jwtToken = authService.signIn(signInRequest).getToken();

        assertTrue(jwtService.isTokenValid(jwtToken, savedUser));
    }
}
