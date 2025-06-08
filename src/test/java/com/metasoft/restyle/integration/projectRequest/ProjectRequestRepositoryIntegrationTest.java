package com.metasoft.restyle.integration.projectRequest;

import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;
import com.metasoft.restyle.platform.projectRequest.infrastructure.persistance.jpa.ProjectRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class ProjectRequestRepositoryIntegrationTest {

    @Autowired
    private ProjectRequestRepository projectRequestRepository;

    private ProjectRequest testRequest;
    private Date deadlineDate;

    @BeforeEach
    void setUp() {
        projectRequestRepository.deleteAll();

        // Setup dates for testing
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        deadlineDate = calendar.getTime();

        // Create a test project request
        testRequest = new ProjectRequest(new CreateProjectRequestCommand(
                "Test Repository Request",
                "Smith",
                "john.smith@example.com",
                "123456789",
                "123 Test St",
                "Test City",
                "Repository test request description",
                1,
                2,
                deadlineDate,
                3,
                5000
        ));
    }

    @Test
    void shouldSaveAndRetrieveProjectRequest() {
        // Act
        ProjectRequest savedRequest = projectRequestRepository.save(testRequest);
        Optional<ProjectRequest> retrievedRequest = projectRequestRepository.findById(savedRequest.getId());

        // Assert
        assertTrue(retrievedRequest.isPresent());
        assertEquals(savedRequest.getId(), retrievedRequest.get().getId());
        assertEquals("Test Repository Request", retrievedRequest.get().getName());
        assertEquals("john.smith@example.com", retrievedRequest.get().getEmail());
    }

    @Test
    void shouldFindProjectRequestsByBusinessId() {
        // Arrange
        projectRequestRepository.save(testRequest);
        projectRequestRepository.save(new ProjectRequest(new CreateProjectRequestCommand(
                "Second Request",
                "Johnson",
                "jane.johnson@example.com",
                "987654321",
                "456 Other St",
                "Other City",
                "Another request for the same business",
                1,
                3,
                deadlineDate,
                2,
                4000
        )));
        projectRequestRepository.save(new ProjectRequest(new CreateProjectRequestCommand(
                "Different Business",
                "Wilson",
                "tom.wilson@example.com",
                "555123456",
                "789 Third St",
                "Third City",
                "Request for different business",
                2,
                4,
                deadlineDate,
                1,
                3000
        )));

        // Act
        List<ProjectRequest> businessRequests = projectRequestRepository.findAllByBusinessId(1L);

        // Assert
        assertEquals(2, businessRequests.size());
        assertTrue(businessRequests.stream()
                .anyMatch(p -> p.getName().equals("Test Repository Request")));
        assertTrue(businessRequests.stream()
                .anyMatch(p -> p.getName().equals("Second Request")));
    }

    @Test
    void shouldFindProjectRequestsByContractorId() {
        // Arrange
        projectRequestRepository.save(testRequest);
        projectRequestRepository.save(new ProjectRequest(new CreateProjectRequestCommand(
                "Same Contractor",
                "Johnson",
                "jane.johnson@example.com",
                "987654321",
                "456 Other St",
                "Other City",
                "Another request for the same contractor",
                3,
                2, // Same contractor ID
                deadlineDate,
                2,
                4000
        )));
        projectRequestRepository.save(new ProjectRequest(new CreateProjectRequestCommand(
                "Different Contractor",
                "Wilson",
                "tom.wilson@example.com",
                "555123456",
                "789 Third St",
                "Third City",
                "Request for different contractor",
                2,
                4,
                deadlineDate,
                1,
                3000
        )));

        // Act
        List<ProjectRequest> contractorRequests = projectRequestRepository.findAllByContractorId(2L);

        // Assert
        assertEquals(2, contractorRequests.size());
        assertTrue(contractorRequests.stream()
                .anyMatch(p -> p.getName().equals("Test Repository Request")));
        assertTrue(contractorRequests.stream()
                .anyMatch(p -> p.getName().equals("Same Contractor")));
    }

    @Test
    void shouldCheckIfProjectRequestNameExists() {
        // Arrange
        projectRequestRepository.save(testRequest);

        // Act & Assert
        assertTrue(projectRequestRepository.existsByName("Test Repository Request"));
        assertFalse(projectRequestRepository.existsByName("Non-existent Request"));
    }

    @Test
    void shouldDeleteProjectRequestById() {
        // Arrange
        ProjectRequest savedRequest = projectRequestRepository.save(testRequest);

        // Act
        projectRequestRepository.deleteById(savedRequest.getId());

        // Assert
        assertFalse(projectRequestRepository.existsById(savedRequest.getId()));
    }
}