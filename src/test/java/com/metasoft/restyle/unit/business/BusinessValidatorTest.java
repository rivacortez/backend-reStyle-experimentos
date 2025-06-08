package com.metasoft.restyle.unit.business;

import com.metasoft.restyle.platform.business.domain.model.commands.CreateBusinessCommand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessValidatorTest {

    @Test
    void createBusinessWithValidDataPassesValidation() {
        // Act & Assert - no exception should be thrown
        assertDoesNotThrow(() -> new CreateBusinessCommand(
                "Valid Business",
                "logo.jpg",
                "Remodeling",
                "123 Main St",
                "New York",
                "Valid business description",
                1
        ));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void createBusinessWithInvalidNameThrowsException(String invalidName) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CreateBusinessCommand(
                        invalidName,
                        "logo.jpg",
                        "Remodeling",
                        "123 Main St",
                        "New York",
                        "Business description",
                        1
                )
        );
        assertEquals("name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void createBusinessWithInvalidExpertiseThrowsException(String invalidExpertise) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CreateBusinessCommand(
                        "Business Name",
                        "logo.jpg",
                        invalidExpertise,
                        "123 Main St",
                        "New York",
                        "Business description",
                        1
                )
        );
        assertEquals("expertise cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void createBusinessWithInvalidAddressThrowsException(String invalidAddress) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CreateBusinessCommand(
                        "Business Name",
                        "logo.jpg",
                        "Remodeling",
                        invalidAddress,
                        "New York",
                        "Business description",
                        1
                )
        );
        assertEquals("address cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void createBusinessWithInvalidCityThrowsException(String invalidCity) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CreateBusinessCommand(
                        "Business Name",
                        "logo.jpg",
                        "Remodeling",
                        "123 Main St",
                        invalidCity,
                        "Business description",
                        1
                )
        );
        assertEquals("city cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void createBusinessWithInvalidDescriptionThrowsException(String invalidDescription) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CreateBusinessCommand(
                        "Business Name",
                        "logo.jpg",
                        "Remodeling",
                        "123 Main St",
                        "New York",
                        invalidDescription,
                        1
                )
        );
        assertEquals("description cannot be null or empty", exception.getMessage());
    }

    @Test
    void createBusinessWithNullRemodelorIdThrowsException() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new CreateBusinessCommand(
                        "Business Name",
                        "logo.jpg",
                        "Remodeling",
                        "123 Main St",
                        "New York",
                        "Business description",
                        null
                )
        );
        assertEquals("remodelerId cannot be null", exception.getMessage());
    }

    @Test
    void businessWithNullImageIsAllowed() {
        // Act & Assert - no exception should be thrown
        assertDoesNotThrow(() -> new CreateBusinessCommand(
                "Valid Business",
                null,
                "Remodeling",
                "123 Main St",
                "New York",
                "Valid business description",
                1
        ));
    }
}