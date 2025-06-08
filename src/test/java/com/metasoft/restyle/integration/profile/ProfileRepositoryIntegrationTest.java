package com.metasoft.restyle.integration.profile;

import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.metasoft.restyle.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class ProfileRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProfileRepository profileRepository;

    @Test
    void shouldSaveProfile() {
        // Arrange
        Profile profile = new Profile(
                "test@example.com",
                "Password123",
                "REMODELER",
                "John",
                "Doe",
                "Smith"
        );

        // Act
        Profile savedProfile = profileRepository.save(profile);

        // Assert
        assertNotNull(savedProfile.getId());
        assertEquals("test@example.com", savedProfile.getEmailAddress());
        assertEquals("John Doe Smith", savedProfile.getFullName());
    }

    @Test
    void shouldFindProfileById() {
        // Arrange
        Profile profile = new Profile(
                "find@example.com",
                "Password123",
                "CONTRACTOR",
                "Jane",
                "Johnson",
                "Williams"
        );
        profile = entityManager.persistAndFlush(profile);

        // Act
        var foundProfile = profileRepository.findById(profile.getId());

        // Assert
        assertTrue(foundProfile.isPresent());
        assertEquals("find@example.com", foundProfile.get().getEmailAddress());
        assertEquals("CONTRACTOR", foundProfile.get().getType());
    }

    @Test
    void shouldFindProfileByEmail() {
        // Arrange
        Profile profile = new Profile(
                "email@example.com",
                "Password123",
                "REMODELER",
                "Robert",
                "Smith",
                "Jones"
        );
        entityManager.persistAndFlush(profile);

        // Act
        var foundProfile = profileRepository.findByEmail(new EmailAddress("email@example.com"));

        // Assert
        assertTrue(foundProfile.isPresent());
        assertEquals("Robert Smith Jones", foundProfile.get().getFullName());
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Arrange
        Profile profile = new Profile(
                "exists@example.com",
                "Password123",
                "CONTRACTOR",
                "Mary",
                "Brown",
                "Davis"
        );
        entityManager.persistAndFlush(profile);

        // Act & Assert
        assertTrue(profileRepository.existsByEmail(new EmailAddress("exists@example.com")));
        assertFalse(profileRepository.existsByEmail(new EmailAddress("nonexistent@example.com")));
    }
}