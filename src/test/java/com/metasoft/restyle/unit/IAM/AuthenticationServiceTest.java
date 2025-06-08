package com.metasoft.restyle.unit.IAM;

import com.metasoft.restyle.platform.iam.application.internal.commandservices.UserCommandServiceImpl;
import com.metasoft.restyle.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.metasoft.restyle.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.commands.SignInCommand;
import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.metasoft.restyle.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @Mock
    private BearerTokenService bearerTokenService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private HttpServletRequest httpRequest;

    private UserCommandServiceImpl userCommandService;

    @BeforeEach
    void setUp() {
        userCommandService = new UserCommandServiceImpl(
                userRepository, hashingService, tokenService, roleRepository);
    }

    @Test
    void handleSignInCommand_withValidCredentials_shouldReturnUserAndToken() {
        SignInCommand command = new SignInCommand("testuser", "password123");

        User testUser = mock(User.class);
        when(testUser.getUsername()).thenReturn("testuser");
        when(testUser.getPassword()).thenReturn("hashed_password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(hashingService.matches("password123", "hashed_password")).thenReturn(true);
        when(tokenService.generateToken("testuser")).thenReturn("valid_token");

        Optional<ImmutablePair<User, String>> result = userCommandService.handle(command);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get().getLeft());
        assertEquals("valid_token", result.get().getRight());
        verify(userRepository).findByUsername("testuser");
        verify(hashingService).matches("password123", "hashed_password");
        verify(tokenService).generateToken("testuser");
    }

    @Test
    void handleSignInCommand_withInvalidUsername_shouldThrowException() {
        SignInCommand command = new SignInCommand("nonexistentuser", "password123");
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class,
                () -> userCommandService.handle(command));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findByUsername("nonexistentuser");
        verify(hashingService, never()).matches(any(), any());
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void handleSignInCommand_withInvalidPassword_shouldThrowException() {
        SignInCommand command = new SignInCommand("testuser", "wrong_password");

        User testUser = mock(User.class);
      //when(testUser.getUsername()).thenReturn("testuser");
        when(testUser.getPassword()).thenReturn("hashed_password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(hashingService.matches("wrong_password", "hashed_password")).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class,
                () -> userCommandService.handle(command));

        assertEquals("Invalid password", exception.getMessage());
        verify(userRepository).findByUsername("testuser");
        verify(hashingService).matches("wrong_password", "hashed_password");
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void tokenValidation_withValidToken_shouldReturnTrue() {
        String validToken = "valid_jwt_token";
        when(bearerTokenService.validateToken(validToken)).thenReturn(true);

        boolean result = bearerTokenService.validateToken(validToken);

        assertTrue(result);
        verify(bearerTokenService).validateToken(validToken);
    }

    @Test
    void extractTokenFromRequest_shouldReturnToken() {
        String validToken = "valid_jwt_token";
        when(bearerTokenService.getBearerTokenFrom(httpRequest)).thenReturn(validToken);

        String result = bearerTokenService.getBearerTokenFrom(httpRequest);

        assertEquals(validToken, result);
        verify(bearerTokenService).getBearerTokenFrom(httpRequest);
    }
}
