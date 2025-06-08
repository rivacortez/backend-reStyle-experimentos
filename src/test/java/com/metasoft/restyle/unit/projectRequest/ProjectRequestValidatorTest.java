package com.metasoft.restyle.unit.projectRequest;

import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectRequestValidatorTest {

    private Date deadlineDate;

    @BeforeEach
    void setUp() {
        // Setup dates for testing
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        deadlineDate = calendar.getTime();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldRejectInvalidName(String name) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    name,
                    "Smith",
                    "test@example.com",
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    1,
                    2,
                    deadlineDate,
                    2,
                    5000
            );
        });
        assertEquals("name cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldRejectInvalidSurname(String surname) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Valid name",
                    surname,
                    "test@example.com",
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    1,
                    2,
                    deadlineDate,
                    2,
                    5000
            );
        });
        assertEquals("surname cannot be null or empty", exception.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void shouldRejectInvalidEmail(String email) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Valid name",
                    "Smith",
                    email,
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    1,
                    2,
                    deadlineDate,
                    2,
                    5000
            );
        });
        assertEquals("email cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldRejectNullBusinessId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Valid name",
                    "Smith",
                    "test@example.com",
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    null,
                    2,
                    deadlineDate,
                    2,
                    5000
            );
        });
        assertEquals("businessId cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullContractorId() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Valid name",
                    "Smith",
                    "test@example.com",
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    1,
                    null,
                    deadlineDate,
                    2,
                    5000
            );
        });
        assertEquals("contractorId cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullDeadlineDate() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Valid name",
                    "Smith",
                    "test@example.com",
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    1,
                    2,
                    null,
                    2,
                    5000
            );
        });
        assertEquals("deadlineDate cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullRooms() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Valid name",
                    "Smith",
                    "test@example.com",
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    1,
                    2,
                    deadlineDate,
                    null,
                    5000
            );
        });
        assertEquals("rooms cannot be null", exception.getMessage());
    }

    @Test
    void shouldRejectNullBudget() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Valid name",
                    "Smith",
                    "test@example.com",
                    "123456789",
                    "123 Main St",
                    "Test City",
                    "Valid summary",
                    1,
                    2,
                    deadlineDate,
                    2,
                    null
            );
        });
        assertEquals("budget cannot be null", exception.getMessage());
    }

    @Test
    void shouldCreateValidProjectRequestCommand() {
        // This should not throw any exceptions
        CreateProjectRequestCommand command = new CreateProjectRequestCommand(
                "Valid name",
                "Smith",
                "test@example.com",
                "123456789",
                "123 Main St",
                "Test City",
                "Valid summary",
                1,
                2,
                deadlineDate,
                2,
                5000
        );

        // Verify all fields are set correctly
        assertEquals("Valid name", command.name());
        assertEquals("Smith", command.surname());
        assertEquals("test@example.com", command.email());
        assertEquals("123456789", command.phone());
        assertEquals("123 Main St", command.address());
        assertEquals("Test City", command.city());
        assertEquals("Valid summary", command.summary());
        assertEquals(1, command.businessId());
        assertEquals(2, command.contractorId());
        assertEquals(deadlineDate, command.deadlineDate());
        assertEquals(2, command.rooms());
        assertEquals(5000, command.budget());
    }
}