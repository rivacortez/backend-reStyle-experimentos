package com.metasoft.restyle.unit.project;

import com.metasoft.restyle.platform.project.domain.model.commands.CreateProjectCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectValidatorTest {

    private Date startDate;
    private Date finishDate;

    @BeforeEach
    void setUp() {
        // Setup dates for testing
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 3);
        finishDate = calendar.getTime();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldRejectInvalidName(String name) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    name,
                    "https://example.com/image.jpg",
                    "Valid description",
                    1,
                    2,
                    startDate,
                    finishDate
            );
        });
        assertEquals("name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldRejectInvalidDescription(String description) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Valid name",
                    "https://example.com/image.jpg",
                    description,
                    1,
                    2,
                    startDate,
                    finishDate
            );
        });
        assertEquals("description cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldRejectNullBusinessId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Valid name",
                    "https://example.com/image.jpg",
                    "Valid description",
                    null,
                    2,
                    startDate,
                    finishDate
            );
        });
        assertEquals("businessId cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullContractorId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Valid name",
                    "https://example.com/image.jpg",
                    "Valid description",
                    1,
                    null,
                    startDate,
                    finishDate
            );
        });
        assertEquals("contractorId cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullStartDate() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Valid name",
                    "https://example.com/image.jpg",
                    "Valid description",
                    1,
                    2,
                    null,
                    finishDate
            );
        });
        assertEquals("startDate cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullFinishDate() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Valid name",
                    "https://example.com/image.jpg",
                    "Valid description",
                    1,
                    2,
                    startDate,
                    null
            );
        });
        assertEquals("finishDate cannot be null", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldRejectInvalidImage(String image) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Valid name",
                    image,
                    "Valid description",
                    1,
                    2,
                    startDate,
                    finishDate
            );
        });
        assertEquals("image cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldCreateValidProjectCommand() {
        // This should not throw any exceptions
        CreateProjectCommand command = new CreateProjectCommand(
                "Valid name",
                "https://example.com/image.jpg",
                "Valid description",
                1,
                2,
                startDate,
                finishDate
        );

        // Verify all fields are set correctly
        assertEquals("Valid name", command.name());
        assertEquals("https://example.com/image.jpg", command.image());
        assertEquals("Valid description", command.description());
        assertEquals(1, command.businessId());
        assertEquals(2, command.contractorId());
        assertEquals(startDate, command.startDate());
        assertEquals(finishDate, command.finishDate());
    }
}