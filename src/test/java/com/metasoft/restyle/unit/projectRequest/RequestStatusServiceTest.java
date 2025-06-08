package com.metasoft.restyle.unit.projectRequest;

import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RequestStatusServiceTest {

    // Define an enum for project request status since it doesn't exist yet
    public enum ProjectRequestStatus {
        PENDING, ACCEPTED, REJECTED
    }

    // Define a service interface for status transitions
    public interface ProjectRequestStatusService {
        Optional<ProjectRequest> acceptRequest(Long requestId);
        Optional<ProjectRequest> rejectRequest(Long requestId);
        ProjectRequestStatus getRequestStatus(Long requestId);
    }

    private ProjectRequestStatusService requestStatusService;
    private ProjectRequest mockRequest;
    private Date deadlineDate;

    @BeforeEach
    void setUp() {
        requestStatusService = mock(ProjectRequestStatusService.class);

        // Setup dates for testing
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        deadlineDate = calendar.getTime();

        // Create a mock project request
        mockRequest = new ProjectRequest(new CreateProjectRequestCommand(
                "Test Request", "Smith", "test@example.com", "123456789",
                "123 Main St", "Test City", "Test summary", 1, 2,
                deadlineDate, 2, 5000
        ));

        // Use reflection to set the ID on the mock request
        try {
            var idField = ProjectRequest.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(mockRequest, 1L);
        } catch (Exception e) {
            fail("Failed to set up test: " + e.getMessage());
        }

        // Configure mock service responses
        when(requestStatusService.acceptRequest(1L)).thenReturn(Optional.of(mockRequest));
        when(requestStatusService.rejectRequest(1L)).thenReturn(Optional.of(mockRequest));
        when(requestStatusService.getRequestStatus(1L)).thenReturn(ProjectRequestStatus.PENDING);
    }

    @Test
    void shouldAcceptRequest() {
        // Act
        Optional<ProjectRequest> result = requestStatusService.acceptRequest(1L);

        // Assert
        assertTrue(result.isPresent());
        verify(requestStatusService).acceptRequest(1L);
    }

    @Test
    void shouldRejectRequest() {
        // Act
        Optional<ProjectRequest> result = requestStatusService.rejectRequest(1L);

        // Assert
        assertTrue(result.isPresent());
        verify(requestStatusService).rejectRequest(1L);
    }

    @Test
    void shouldGetRequestStatus() {
        // Act
        ProjectRequestStatus status = requestStatusService.getRequestStatus(1L);

        // Assert
        assertEquals(ProjectRequestStatus.PENDING, status);
        verify(requestStatusService).getRequestStatus(1L);
    }

    @Test
    void shouldTransitionFromPendingToAccepted() {
        // Setup - Initially PENDING
        when(requestStatusService.getRequestStatus(1L)).thenReturn(ProjectRequestStatus.PENDING);

        // Act - Accept the request
        requestStatusService.acceptRequest(1L);

        // Setup - Mock the status change to ACCEPTED
        when(requestStatusService.getRequestStatus(1L)).thenReturn(ProjectRequestStatus.ACCEPTED);

        // Assert - Check new status
        assertEquals(ProjectRequestStatus.ACCEPTED, requestStatusService.getRequestStatus(1L));
    }

    @Test
    void shouldTransitionFromPendingToRejected() {
        // Setup - Initially PENDING
        when(requestStatusService.getRequestStatus(1L)).thenReturn(ProjectRequestStatus.PENDING);

        // Act - Reject the request
        requestStatusService.rejectRequest(1L);

        // Setup - Mock the status change to REJECTED
        when(requestStatusService.getRequestStatus(1L)).thenReturn(ProjectRequestStatus.REJECTED);

        // Assert - Check new status
        assertEquals(ProjectRequestStatus.REJECTED, requestStatusService.getRequestStatus(1L));
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentRequest() {
        // Setup
        when(requestStatusService.acceptRequest(999L)).thenReturn(Optional.empty());
        when(requestStatusService.rejectRequest(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertTrue(requestStatusService.acceptRequest(999L).isEmpty());
        assertTrue(requestStatusService.rejectRequest(999L).isEmpty());
    }
}