package com.metasoft.restyle.unit.profile;

import com.metasoft.restyle.platform.profiles.application.internal.queryservices.ProfileQueryServiceImpl;
import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import com.metasoft.restyle.platform.profiles.domain.model.queries.GetProfileByEmailQuery;
import com.metasoft.restyle.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.metasoft.restyle.platform.profiles.domain.model.valueobjects.EmailAddress;
import com.metasoft.restyle.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ProfileQueryServiceImplTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileQueryServiceImpl profileQueryService;

    private Profile testProfile1;
    private Profile testProfile2;
    private EmailAddress emailAddress1;
    private EmailAddress emailAddress2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create test profiles
        testProfile1 = new Profile(
                "john@example.com",
                "Password123",
                "REMODELER",
                "John",
                "Doe",
                "Smith"
        );
        testProfile2 = new Profile(
                "jane@example.com",
                "SecurePass456",
                "CONTRACTOR",
                "Jane",
                "Johnson",
                "Williams"
        );

        // Set IDs using reflection since there's no setter method
        try {
            var field = Profile.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(testProfile1, 1L);
            field.set(testProfile2, 2L);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }

        // Create EmailAddress objects for testing
        emailAddress1 = new EmailAddress("john@example.com");
        emailAddress2 = new EmailAddress("jane@example.com");
    }

    @Test
    void shouldGetProfileById() {
        // Arrange
        when(profileRepository.findById(1L)).thenReturn(Optional.of(testProfile1));
        when(profileRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        var result1 = profileQueryService.handle(new GetProfileByIdQuery(1L));
        var result999 = profileQueryService.handle(new GetProfileByIdQuery(999L));

        // Assert
        assertTrue(result1.isPresent());
        assertEquals("john@example.com", result1.get().getEmailAddress());
        assertEquals("John Doe Smith", result1.get().getFullName());

        assertTrue(result999.isEmpty());

        verify(profileRepository, times(1)).findById(1L);
        verify(profileRepository, times(1)).findById(999L);
    }

    @Test
    void shouldGetProfileByEmail() {
        // Arrange
        when(profileRepository.findByEmail(any(EmailAddress.class))).thenAnswer(invocation -> {
            EmailAddress email = invocation.getArgument(0);
            if ("john@example.com".equals(email.address())) {
                return Optional.of(testProfile1);
            } else if ("jane@example.com".equals(email.address())) {
                return Optional.of(testProfile2);
            }
            return Optional.empty();
        });

        // Act
        var result1 = profileQueryService.handle(new GetProfileByEmailQuery(emailAddress1));
        var result2 = profileQueryService.handle(new GetProfileByEmailQuery(emailAddress2));
        var resultNonExistent = profileQueryService.handle(
                new GetProfileByEmailQuery(new EmailAddress("nonexistent@example.com"))
        );

        // Assert
        assertTrue(result1.isPresent());
        assertEquals(1L, result1.get().getId());
        assertEquals("John Doe Smith", result1.get().getFullName());

        assertTrue(result2.isPresent());
        assertEquals(2L, result2.get().getId());
        assertEquals("Jane Johnson Williams", result2.get().getFullName());

        assertTrue(resultNonExistent.isEmpty());

        verify(profileRepository, times(3)).findByEmail(any(EmailAddress.class));
    }

    @Test
    void shouldGetAllProfiles() {
        // Arrange
        List<Profile> allProfiles = Arrays.asList(testProfile1, testProfile2);
        when(profileRepository.findAll()).thenReturn(allProfiles);

        // Act
        List<Profile> result = profileQueryService.handle(new GetAllProfilesQuery());

        // Assert
        assertEquals(2, result.size());
        assertEquals("john@example.com", result.get(0).getEmailAddress());
        assertEquals("Jane Johnson Williams", result.get(1).getFullName());

        verify(profileRepository, times(1)).findAll();
    }
}