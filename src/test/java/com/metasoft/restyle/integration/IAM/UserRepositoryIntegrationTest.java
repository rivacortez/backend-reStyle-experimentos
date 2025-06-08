package com.metasoft.restyle.integration.IAM;

import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.entities.Role;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role contractorRole;

    @BeforeEach
    void setUp() {
        if (!roleRepository.existsByName(Roles.ROLE_CONTRACTOR)) {
            contractorRole = roleRepository.save(new Role(Roles.ROLE_CONTRACTOR));
        } else {
            contractorRole = roleRepository.findByName(Roles.ROLE_CONTRACTOR).orElseThrow();
        }
    }

    @Test
    void shouldSaveAndRetrieveUser() {
        // Arrange
        String uniqueUsername = "testuser_" + UUID.randomUUID();
        String uniqueEmail = "test_" + UUID.randomUUID() + "@example.com";
        User user = createTestUser(uniqueUsername, uniqueEmail);
        user.addRole(contractorRole);

        // Act
        User savedUser = userRepository.save(user);
        User retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);

        // Assert
        assertNotNull(retrievedUser);
        assertEquals(uniqueUsername, retrievedUser.getUsername());
        assertEquals(uniqueEmail, retrievedUser.getEmail());
        assertEquals(1, retrievedUser.getRoles().size());
        assertTrue(retrievedUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals(Roles.ROLE_CONTRACTOR)));
    }

    @Test
    void shouldFindUserByUsername() {
        // Arrange
        User user = createTestUser("findByUsernameTest", "findByUsernameTest@example.com");
        user.addRole(contractorRole);
        userRepository.save(user);

        // Act
        User foundUser = userRepository.findByUsername("findByUsernameTest").orElse(null);

        // Assert
        assertNotNull(foundUser);
        assertEquals("findByUsernameTest", foundUser.getUsername());
    }

    @Test
    void shouldReturnEmptyWhenFindingNonExistentUsername() {
        // Act
        var result = userRepository.findByUsername("nonexistentuser");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCheckIfUsernameExists() {
        // Arrange
        User user = createTestUser("existsTest", "existsTest@example.com");
        user.addRole(contractorRole);
        userRepository.save(user);

        // Act & Assert
        assertTrue(userRepository.existsByUsername("existsTest"));
        assertFalse(userRepository.existsByUsername("nonexistentuser"));
    }

    @Test
    void shouldUpdateUserData() {
        // Arrange
        User user = createTestUser("updateTest", "updateTest@example.com");
        user.addRole(contractorRole);
        User savedUser = userRepository.save(user);

        // Act - Update user properties
        savedUser.setEmail("updated@example.com");
        savedUser.setDescription("Updated description");
        userRepository.save(savedUser);

        // Retrieve updated user
        User updatedUser = userRepository.findById(savedUser.getId()).orElseThrow();

        // Assert
        assertEquals("updated@example.com", updatedUser.getEmail());
        assertEquals("Updated description", updatedUser.getDescription());
        assertEquals("updateTest", updatedUser.getUsername());
    }

    private User createTestUser(String username, String email) {
        return new User(
                username,
                "password123",
                email,
                "Test",
                "User",
                "",
                "Description",
                "123456789",
                "image.jpg"
        );
    }
}
