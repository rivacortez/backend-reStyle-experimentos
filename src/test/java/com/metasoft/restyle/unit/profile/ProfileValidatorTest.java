package com.metasoft.restyle.unit.profile;

import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.PersonName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ProfileValidatorTest {

    @Test
    void shouldCreateValidEmailAddress() {
        // Act & Assert
        EmailAddress emailAddress = new EmailAddress("valid@example.com");
        assertEquals("valid@example.com", emailAddress.address());
    }

    @Test
    void shouldCreateEmptyEmailAddress() {
        // Act & Assert - testing default constructor
        EmailAddress emailAddress = new EmailAddress();
        assertNull(emailAddress.address());
    }

    @Test
    void shouldCreateValidPersonName() {
        // Act & Assert
        PersonName personName = new PersonName("John", "Doe", "Smith");
        assertEquals("John Doe Smith", personName.getFullName());
        assertEquals("John", personName.firstName());
        assertEquals("Doe", personName.paternalLastName());
        assertEquals("Smith", personName.maternalLastName());
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidFirstName(String invalidName) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new PersonName(invalidName, "Doe", "Smith"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidPaternalLastName(String invalidName) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new PersonName("John", invalidName, "Smith"));
    }

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = {"   "})
    void shouldRejectInvalidMaternalLastName(String invalidName) {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> new PersonName("John", "Doe", invalidName));
    }

    @Test
    void shouldCreateProfileWithValidData() {
        // Act
        Profile profile = new Profile(
                "john@example.com",
                "Password123",
                "REMODELER",
                "John",
                "Doe",
                "Smith"
        );

        // Assert
        assertEquals("john@example.com", profile.getEmailAddress());
        assertEquals("Password123", profile.getPassword());
        assertEquals("REMODELER", profile.getType());
        assertEquals("John Doe Smith", profile.getFullName());
    }

    @Test
    void shouldUpdateProfileName() {
        // Arrange
        Profile profile = new Profile(
                "john@example.com",
                "Password123",
                "REMODELER",
                "John",
                "Doe",
                "Smith"
        );

        // Act
        profile.updateName("Jane", "Johnson", "Williams");

        // Assert
        assertEquals("Jane Johnson Williams", profile.getFullName());
    }
}