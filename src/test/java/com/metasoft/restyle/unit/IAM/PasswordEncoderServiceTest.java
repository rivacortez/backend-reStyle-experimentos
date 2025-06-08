package com.metasoft.restyle.unit.IAM;

import com.metasoft.restyle.platform.iam.infrastructure.hashing.bcrypt.BCryptHashingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordEncoderServiceTest {

    @Mock
    private BCryptHashingService hashingService;

    private BCryptPasswordEncoder realEncoder;

    @BeforeEach
    void setUp() {
        // Create a real BCryptPasswordEncoder for actual encoding/matching behavior
        realEncoder = new BCryptPasswordEncoder();

        // Configure mock to delegate to real encoder
        when(hashingService.encode(anyString())).thenAnswer(invocation -> {
            String rawPassword = invocation.getArgument(0);
            return realEncoder.encode(rawPassword);
        });

        when(hashingService.matches(anyString(), anyString())).thenAnswer(invocation -> {
            String rawPassword = invocation.getArgument(0);
            String encodedPassword = invocation.getArgument(1);
            return realEncoder.matches(rawPassword, encodedPassword);
        });
    }

    /*
    @Test
    void encodeShouldGenerateDifferentHashesForSamePassword() {
        // Arrange
        String password = "securePassword123";

        // Act
        String firstHash = hashingService.encode(password);
        String secondHash = hashingService.encode(password);

        // Assert
        assertNotEquals(firstHash, secondHash, "BCrypt should generate different hashes due to random salt");
    }
 */
    /*
    @Test
    void encodedPasswordShouldBeDifferentFromOriginal() {
        // Arrange
        String password = "securePassword123";

        // Act
        String encodedPassword = hashingService.encode(password);

        // Assert
        assertNotEquals(password, encodedPassword, "Encoded password should be different from original");
    }
*/
    @Test
    void matchesShouldReturnTrueForCorrectPassword() {
        // Arrange
        String password = "securePassword123";
        String encodedPassword = hashingService.encode(password);

        // Act
        boolean result = hashingService.matches(password, encodedPassword);

        // Assert
        assertTrue(result, "Matches should return true for correct password");
    }

    @Test
    void matchesShouldReturnFalseForIncorrectPassword() {
        // Arrange
        String correctPassword = "securePassword123";
        String wrongPassword = "incorrectPassword";
        String encodedPassword = hashingService.encode(correctPassword);

        // Act
        boolean result = hashingService.matches(wrongPassword, encodedPassword);

        // Assert
        assertFalse(result, "Matches should return false for incorrect password");
    }

    @Test
    void encodeShouldHandleEmptyPassword() {
        // Arrange
        String emptyPassword = "";

        // Act & Assert
        assertDoesNotThrow(() -> {
            String encodedPassword = hashingService.encode(emptyPassword);
            assertTrue(hashingService.matches(emptyPassword, encodedPassword));
        });
    }
}