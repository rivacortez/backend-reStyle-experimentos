package com.metasoft.restyle.unit.projectRequest;

import com.metasoft.restyle.platform.projectRequest.application.internal.commandservices.ProjectRequestCommandServiceImpl;
import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.model.commands.CreateProjectRequestCommand;
import com.metasoft.restyle.platform.projectRequest.infrastructure.persistance.jpa.ProjectRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectRequestCommandServiceImplTest {

    @Autowired
    private ProjectRequestCommandServiceImpl projectRequestCommandService;

    @Autowired
    private ProjectRequestRepository projectRequestRepository;

    private Date deadlineDate;

    @BeforeEach
    void setUp() {
        projectRequestRepository.deleteAll();

        // Setup deadline date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        deadlineDate = calendar.getTime();
    }

    @Test
    void shouldCreateNewProjectRequest() {
        // Arrange
        CreateProjectRequestCommand command = new CreateProjectRequestCommand(
                "Test Request",
                "Smith",
                "john.smith@example.com",
                "123456789",
                "123 Test St",
                "Test City",
                "Kitchen renovation summary",
                1,
                2,
                deadlineDate,
                3,
                5000
        );

        // Act
        Optional<ProjectRequest> result = projectRequestCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        ProjectRequest request = result.get();
        assertNotNull(request.getId());
        assertEquals("Test Request", request.getName());
        assertEquals("Smith", request.getSurname());
        assertEquals("john.smith@example.com", request.getEmail());
        assertEquals(1, request.getBusinessId());
        assertEquals(2, request.getContractorId());
        assertEquals(5000, request.getBudget());
    }

    @Test
    void shouldThrowExceptionWhenProjectRequestNameExists() {
        // Arrange - Create and save a project request
        CreateProjectRequestCommand firstCommand = new CreateProjectRequestCommand(
                "Duplicate Request",
                "Smith",
                "john.smith@example.com",
                "123456789",
                "123 Test St",
                "Test City",
                "First summary",
                1,
                2,
                deadlineDate,
                2,
                3000
        );
        projectRequestCommandService.handle(firstCommand);

        // Create another project request with the same name
        CreateProjectRequestCommand duplicateCommand = new CreateProjectRequestCommand(
                "Duplicate Request",
                "Johnson",
                "jane.johnson@example.com",
                "987654321",
                "456 Other St",
                "Other City",
                "Second summary with same name",
                3,
                4,
                deadlineDate,
                1,
                4000
        );

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectRequestCommandService.handle(duplicateCommand);
        });

        assertEquals("Project Request with same name already exists", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidProjectRequestData() {
        // Test is handled by validation in CreateProjectRequestCommand constructor
        // We'll verify exceptions are thrown for invalid data

        Exception nameException = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    null,
                    "Smith",
                    "john.smith@example.com",
                    "123456789",
                    "123 Test St",
                    "Test City",
                    "Summary",
                    1,
                    2,
                    deadlineDate,
                    3,
                    5000
            );
        });
        assertEquals("name cannot be null or empty", nameException.getMessage());

        Exception emailException = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectRequestCommand(
                    "Test Request",
                    "Smith",
                    "",
                    "123456789",
                    "123 Test St",
                    "Test City",
                    "Summary",
                    1,
                    2,
                    deadlineDate,
                    3,
                    5000
            );
        });
        assertEquals("email cannot be null or empty", emailException.getMessage());
    }
}