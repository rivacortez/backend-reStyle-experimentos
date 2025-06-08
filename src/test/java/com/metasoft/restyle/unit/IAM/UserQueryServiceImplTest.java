package com.metasoft.restyle.unit.IAM;

import com.metasoft.restyle.platform.iam.application.internal.queryservices.UserQueryServiceImpl;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.metasoft.restyle.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.metasoft.restyle.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserQueryServiceImpl userQueryService;

    @BeforeEach
    void setUp() {
        userQueryService = new UserQueryServiceImpl(userRepository);
    }

    /*
    @Test
    void handleGetAllUsersQuery_shouldReturnAllUsers() {
        // Arrange
        User user1 = mock(User.class);
        User user2 = mock(User.class);
        List<User> expectedUsers = List.of(user1, user2);

        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> result = userQueryService.handle(new GetAllUsersQuery());

        // Assert
        assertEquals(2, result.size());
        assertEquals(expectedUsers, result);
        verify(userRepository).findAll();
    }
*/
    /*
    @Test
    void handleGetUserByIdQuery_shouldReturnUserWhenExists() {
        // Arrange
        Long userId = 1L;
        User expectedUser = mock(User.class);
        when(expectedUser.getId()).thenReturn(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        // Act
        Optional<User> result = userQueryService.handle(new GetUserByIdQuery(userId));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepository).findById(userId);
    }
*/
    @Test
    void handleGetUserByIdQuery_shouldReturnEmptyWhenUserDoesNotExist() {
        // Arrange
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userQueryService.handle(new GetUserByIdQuery(userId));

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
    }

    /*
    @Test
    void handleGetUserByUsernameQuery_shouldReturnUserWhenExists() {
        // Arrange
        String username = "testuser";
        User expectedUser = mock(User.class);
        when(expectedUser.getUsername()).thenReturn(username);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        // Act
        Optional<User> result = userQueryService.handle(new GetUserByUsernameQuery(username));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser, result.get());
        verify(userRepository).findByUsername(username);
    }
*/
    @Test
    void handleGetUserByUsernameQuery_shouldReturnEmptyWhenUserDoesNotExist() {
        // Arrange
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userQueryService.handle(new GetUserByUsernameQuery(username));

        // Assert
        assertFalse(result.isPresent());
        verify(userRepository).findByUsername(username);
    }
}