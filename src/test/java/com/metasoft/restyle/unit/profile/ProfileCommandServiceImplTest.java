package com.metasoft.restyle.unit.profile;

import com.metasoft.restyle.platform.profiles.application.internal.commandservices.ProfileCommandServiceImpl;
import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.metasoft.restyle.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ProfileCommandServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileCommandServiceImpl profileCommandService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProfileSuccessfully() {
        // Arrange
        CreateProfileCommand command = new CreateProfileCommand(
                "john@example.com",
                "Password123",
                "REMODELER",
                "John",
                "Doe",
                "Smith"
        );

        // Mock repository to return empty Optional (email doesn't exist)
        when(profileRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.empty());

        // Use doAnswer to set the ID on any profile that gets saved
        doAnswer(invocation -> {
            Profile profileToSave = invocation.getArgument(0);
            // Set the ID on the actual profile being saved
            try {
                var field = Profile.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(profileToSave, 1L);
            } catch (Exception e) {
                fail("Reflection failed: " + e.getMessage());
            }
            return profileToSave;
        }).when(profileRepository).save(any(Profile.class));

        // Act
        Long profileId = profileCommandService.handle(command);

        // Assert
        assertEquals(1L, profileId);
        verify(profileRepository, times(1)).findByEmail(any(EmailAddress.class));
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        CreateProfileCommand command = new CreateProfileCommand(
                "existing@example.com",
                "Password123",
                "REMODELER",
                "John",
                "Doe",
                "Smith"
        );

        // Mock existing profile with the same email
        Profile existingProfile = new Profile(
                "existing@example.com",
                "DifferentPassword",
                "CONTRACTOR",
                "Existing",
                "User",
                "Name"
        );

        when(profileRepository.findByEmail(any(EmailAddress.class))).thenReturn(Optional.of(existingProfile));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> profileCommandService.handle(command)
        );

        assertTrue(exception.getMessage().contains("already exists"));
        verify(profileRepository, times(1)).findByEmail(any(EmailAddress.class));
        verify(profileRepository, never()).save(any(Profile.class));
    }
}