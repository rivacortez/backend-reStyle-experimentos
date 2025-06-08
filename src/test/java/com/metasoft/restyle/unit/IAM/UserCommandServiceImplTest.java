package com.metasoft.restyle.unit.IAM;

import com.metasoft.restyle.platform.iam.application.internal.commandservices.UserCommandServiceImpl;
import com.metasoft.restyle.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.metasoft.restyle.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.commands.SignInCommand;
import com.metasoft.restyle.platform.iam.domain.model.commands.SignUpCommand;
import com.metasoft.restyle.platform.iam.domain.model.commands.UpdateUserCommand;
import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @Mock
    private RoleRepository roleRepository;

    private UserCommandServiceImpl userCommandService;

    @BeforeEach
    void setUp() {
        userCommandService = new UserCommandServiceImpl(
                userRepository, hashingService, tokenService, roleRepository);
    }

    /*
    @Test
    void handleSignUpCommand_shouldCreateNewUser() {
        // Arrange
        Role role = new Role(1L, Roles.ROLE_CONTRACTOR);
        List<Role> roles = List.of(role);
        SignUpCommand command = new SignUpCommand(
                "testuser", "password", roles,
                "test@example.com", "Test", "User", "",
                "Description", "123456789", "image.jpg");

        // Create a mock for the saved User
        User savedUser = mock(User.class);
        when(savedUser.getId()).thenReturn(1L);
        when(savedUser.getUsername()).thenReturn("testuser");

        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(hashingService.encode("password")).thenReturn("hashed_password");
        when(roleRepository.findByName(Roles.ROLE_CONTRACTOR)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).save(any(User.class));
    }
*/
    @Test
    void handleSignUpCommand_shouldThrowExceptionWhenUsernameExists() {
        // Arrange
        SignUpCommand command = new SignUpCommand(
                "existinguser", "password", List.of(),
                "test@example.com", "Test", "User", "",
                "Description", "123456789", "image.jpg");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
        verify(userRepository, never()).save(any(User.class));
    }

    /*
    @Test
    void handleUpdateUserCommand_shouldUpdateExistingUser() {
        // Arrange
        Long userId = 1L;
        UpdateUserCommand command = new UpdateUserCommand(
                userId, "updated@example.com", "Updated description",
                "987654321", "updated.jpg");

        // Create mocks for the existing and updated users
        User existingUser = mock(User.class);
        when(existingUser.getId()).thenReturn(userId);

        User updatedUser = mock(User.class);
        when(updatedUser.getId()).thenReturn(userId);
        when(updatedUser.getEmail()).thenReturn("updated@example.com");
        when(updatedUser.getDescription()).thenReturn("Updated description");
        when(updatedUser.getPhone()).thenReturn("987654321");
        when(updatedUser.getImage()).thenReturn("updated.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(updatedUser));

        // Act
        Optional<User> result = userCommandService.hadle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("updated@example.com", result.get().getEmail());
        assertEquals("Updated description", result.get().getDescription());
        assertEquals("987654321", result.get().getPhone());
        assertEquals("updated.jpg", result.get().getImage());
        verify(userRepository).save(any(User.class));
    }
*/
    @Test
    void handleUpdateUserCommand_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        Long userId = 999L;
        UpdateUserCommand command = new UpdateUserCommand(
                userId, "updated@example.com", "Updated description",
                "987654321", "updated.jpg");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userCommandService.hadle(command));
        verify(userRepository, never()).save(any(User.class));
    }

    /*
    @Test
    void handleSignInCommand_shouldAuthenticateValidUser() {
        // Arrange
        SignInCommand command = new SignInCommand("testuser", "password");

        // Create a mock for the user
        User user = mock(User.class);
        when(user.getId()).thenReturn(1L);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getPassword()).thenReturn("hashed_password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(hashingService.matches("password", "hashed_password")).thenReturn(true);
        when(tokenService.generateToken("testuser")).thenReturn("jwt_token");

        // Act
        Optional<ImmutablePair<User, String>> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user, result.get().getLeft());
        assertEquals("jwt_token", result.get().getRight());
    }*/

    @Test
    void handleSignInCommand_shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        SignInCommand command = new SignInCommand("nonexistentuser", "password");

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
    }
 /*
    @Test
    void handleSignInCommand_shouldThrowExceptionWhenPasswordIsInvalid() {
        // Arrange
        SignInCommand command = new SignInCommand("testuser", "wrongpassword");

        // Create a mock for the user
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("testuser");
        when(user.getPassword()).thenReturn("hashed_password");

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(hashingService.matches("wrongpassword", "hashed_password")).thenReturn(false);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
    }*/
}