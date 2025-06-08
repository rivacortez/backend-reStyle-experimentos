package com.metasoft.restyle.unit.project;


import com.metasoft.restyle.platform.project.application.internal.commandservices.ProjectCommandServiceImpl;
import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.model.commands.CreateProjectCommand;
import com.metasoft.restyle.platform.project.infrastructure.persistance.jpa.ProjectRepository;
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
class ProjectCommandServiceImplTest {

    @Autowired
    private ProjectCommandServiceImpl projectCommandService;

    @Autowired
    private ProjectRepository projectRepository;

    private Date startDate;
    private Date finishDate;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        projectRepository.deleteAll();

        // Setup common test dates
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 3);
        finishDate = calendar.getTime();
    }

    @Test
    void shouldCreateNewProject() {
        // Arrange
        CreateProjectCommand command = new CreateProjectCommand(
                "Test Project",
                "https://example.com/image.jpg",
                "This is a test project description",
                1,
                2,
                startDate,
                finishDate
        );

        // Act
        Optional<Project> result = projectCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        Project project = result.get();
        assertNotNull(project.getId());
        assertEquals("Test Project", project.getName());
        assertEquals("https://example.com/image.jpg", project.getImage());
        assertEquals("This is a test project description", project.getDescription());
        assertEquals(1, project.getBusinessId());
        assertEquals(2, project.getContractorId());
        assertEquals(startDate, project.getStartDate());
        assertEquals(finishDate, project.getFinishDate());
    }

    @Test
    void shouldThrowExceptionWhenProjectNameExists() {
        // Arrange - Create and save a project
        CreateProjectCommand firstCommand = new CreateProjectCommand(
                "Duplicate Project",
                "https://example.com/image1.jpg",
                "First project",
                1,
                2,
                startDate,
                finishDate
        );
        projectCommandService.handle(firstCommand);

        // Create another project with the same name
        CreateProjectCommand duplicateCommand = new CreateProjectCommand(
                "Duplicate Project",
                "https://example.com/image2.jpg",
                "Second project with same name",
                3,
                4,
                startDate,
                finishDate
        );

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            projectCommandService.handle(duplicateCommand);
        });

        assertEquals("Project with same name already exists", exception.getMessage());
    }

    @Test
    void shouldRejectInvalidProjectData() {
        // Arrange - Missing name
        Exception nameException = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    null,
                    "https://example.com/image.jpg",
                    "Description",
                    1,
                    2,
                    startDate,
                    finishDate
            );
        });
        assertEquals("name cannot be null or empty", nameException.getMessage());

        // Arrange - Missing description
        Exception descException = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Test Project",
                    "https://example.com/image.jpg",
                    "",
                    1,
                    2,
                    startDate,
                    finishDate
            );
        });
        assertEquals("description cannot be null or empty", descException.getMessage());

        // Arrange - Missing date
        Exception dateException = assertThrows(IllegalArgumentException.class, () -> {
            new CreateProjectCommand(
                    "Test Project",
                    "https://example.com/image.jpg",
                    "Description",
                    1,
                    2,
                    null,
                    finishDate
            );
        });
        assertEquals("startDate cannot be null", dateException.getMessage());
    }

    @Test
    void shouldCreateMultipleProjects() {
        // Arrange
        CreateProjectCommand command1 = new CreateProjectCommand(
                "Project 1",
                "https://example.com/image1.jpg",
                "Description 1",
                1,
                1,
                startDate,
                finishDate
        );

        CreateProjectCommand command2 = new CreateProjectCommand(
                "Project 2",
                "https://example.com/image2.jpg",
                "Description 2",
                1,
                2,
                startDate,
                finishDate
        );

        // Act
        Optional<Project> result1 = projectCommandService.handle(command1);
        Optional<Project> result2 = projectCommandService.handle(command2);

        // Assert
        assertTrue(result1.isPresent());
        assertTrue(result2.isPresent());

        // Verify they have different IDs
        assertNotEquals(result1.get().getId(), result2.get().getId());

        // Verify total count in database
        assertEquals(2, projectRepository.count());
    }
}