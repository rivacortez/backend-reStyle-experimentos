package com.metasoft.restyle.unit.IAM;

import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.application.internal.commandservices.UserCommandServiceImpl;
import com.metasoft.restyle.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.metasoft.restyle.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.metasoft.restyle.platform.iam.domain.model.commands.SignUpCommand;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleAssignmentServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    private UserCommandServiceImpl userCommandService;

    private User testUser;

    @BeforeEach
    void setUp() {
        userCommandService = new UserCommandServiceImpl(
                userRepository, hashingService, tokenService, roleRepository);

        testUser = new User(
                "testuser",
                "password",
                "test@example.com",
                "Test",
                "User",
                "Surname",
                "Description",
                "123456789",
                "image.jpg"
        );
    }

    @Test
    void shouldAssignSingleRoleToUser() {
        // Arrange
        Role role = new Role(1L, Roles.ROLE_REMODELER);

        // Act
        User result = testUser.addRole(role);

        // Assert
        assertEquals(1, result.getRoles().size());
        assertTrue(result.getRoles().contains(role));
    }

    @Test
    void shouldAssignMultipleRolesToUser() {
        // Arrange
        Role adminRole = new Role(1L, Roles.ROLE_ADMIN);
        Role remodelerRole = new Role(2L, Roles.ROLE_REMODELER);
        List<Role> roles = Arrays.asList(adminRole, remodelerRole);

        // Act
        User result = testUser.addRoles(roles);

        // Assert
        assertEquals(2, result.getRoles().size());
        assertTrue(result.getRoles().contains(adminRole));
        assertTrue(result.getRoles().contains(remodelerRole));
    }

    @Test
    void shouldAssignDefaultRoleWhenNullRoleIsProvided() {
        // Act
        User result = testUser.addRole(null);

        // Assert
        assertEquals(1, result.getRoles().size());
        assertEquals(Roles.ROLE_CONTRACTOR, result.getRoles().iterator().next().getName());
    }

    @Test
    void shouldAssignDefaultRoleWhenEmptyRoleListIsProvided() {
        // Act
        User result = testUser.addRoles(new ArrayList<>());

        // Assert
        assertEquals(1, result.getRoles().size());
        assertEquals(Roles.ROLE_CONTRACTOR, result.getRoles().iterator().next().getName());
    }

    @Test
    void shouldCreateUserWithSpecifiedRoles() {
        // Arrange
        Role adminRole = new Role(1L, Roles.ROLE_ADMIN);
        Role remodelerRole = new Role(2L, Roles.ROLE_REMODELER);

        when(roleRepository.findByName(Roles.ROLE_ADMIN)).thenReturn(Optional.of(adminRole));
        when(roleRepository.findByName(Roles.ROLE_REMODELER)).thenReturn(Optional.of(remodelerRole));

        when(hashingService.encode(any())).thenReturn("hashed_password");

        User savedUser = new User(
                "newuser",
                "hashed_password",
                "user@example.com",
                "New",
                "User",
                "Test",
                "Description",
                "123456789",
                "image.jpg"
        );
        savedUser.addRoles(Arrays.asList(adminRole, remodelerRole));

        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        SignUpCommand command = new SignUpCommand(
                "newuser",
                "password",
                Arrays.asList(
                        new Role(null, Roles.ROLE_ADMIN),
                        new Role(null, Roles.ROLE_REMODELER)
                ),
                "user@example.com",
                "New",
                "User",
                "Test",
                "Description",
                "123456789",
                "image.jpg"
        );

        // Act
        Optional<User> result = userCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        Set<Role> resultRoles = result.get().getRoles();
        assertEquals(2, resultRoles.size());

        boolean hasAdminRole = resultRoles.stream().anyMatch(r -> r.getName() == Roles.ROLE_ADMIN);
        boolean hasRemodelerRole = resultRoles.stream().anyMatch(r -> r.getName() == Roles.ROLE_REMODELER);

        assertTrue(hasAdminRole);
        assertTrue(hasRemodelerRole);
    }

    @Test
    void shouldThrowExceptionWhenRoleNotFound() {
        // Arrange
        when(roleRepository.findByName(Roles.ROLE_ADMIN)).thenReturn(Optional.empty());
        when(userRepository.existsByUsername("newuser")).thenReturn(false);

        SignUpCommand command = new SignUpCommand(
                "newuser",
                "password",
                List.of(new Role(null, Roles.ROLE_ADMIN)),
                "user@example.com",
                "New",
                "User",
                "Test",
                "Description",
                "123456789",
                "image.jpg"
        );

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userCommandService.handle(command));
    }
}