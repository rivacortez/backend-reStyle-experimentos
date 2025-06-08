package com.metasoft.restyle.integration.project;

import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.model.commands.CreateProjectCommand;
import com.metasoft.restyle.platform.project.infrastructure.persistance.jpa.ProjectRepository;
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
public class ProjectRepositoryIntegrationTest {

    @Autowired
    private ProjectRepository projectRepository;

    private Project testProject;
    private Date startDate;
    private Date finishDate;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();

        // Setup dates for testing
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 3);
        finishDate = calendar.getTime();

        // Create a test project
        testProject = new Project(new CreateProjectCommand(
                "Test Repository Project",
                "https://example.com/test-image.jpg",
                "Repository test project description",
                1,
                2,
                startDate,
                finishDate
        ));
    }

    @Test
    void shouldSaveAndRetrieveProject() {
        // Act
        Project savedProject = projectRepository.save(testProject);
        Optional<Project> retrievedProject = projectRepository.findById(savedProject.getId());

        // Assert
        assertTrue(retrievedProject.isPresent());
        assertEquals(savedProject.getId(), retrievedProject.get().getId());
        assertEquals("Test Repository Project", retrievedProject.get().getName());
        assertEquals("Repository test project description", retrievedProject.get().getDescription());
    }

    @Test
    void shouldFindProjectsByBusinessId() {
        // Arrange
        projectRepository.save(testProject);
        projectRepository.save(new Project(new CreateProjectCommand(
                "Second Project",
                "https://example.com/image2.jpg",
                "Another project for the same business",
                1,
                3,
                startDate,
                finishDate
        )));
        projectRepository.save(new Project(new CreateProjectCommand(
                "Different Business",
                "https://example.com/image3.jpg",
                "Project for different business",
                2,
                4,
                startDate,
                finishDate
        )));

        // Act
        List<Project> businessProjects = projectRepository.findAllByBusinessId(1L);

        // Assert
        assertEquals(2, businessProjects.size());
        assertTrue(businessProjects.stream()
                .anyMatch(p -> p.getName().equals("Test Repository Project")));
        assertTrue(businessProjects.stream()
                .anyMatch(p -> p.getName().equals("Second Project")));
    }

    @Test
    void shouldCheckIfProjectNameExists() {
        // Arrange
        projectRepository.save(testProject);

        // Act & Assert
        assertTrue(projectRepository.existsByName("Test Repository Project"));
        assertFalse(projectRepository.existsByName("Non-existent Project"));
    }

    @Test
    void shouldDeleteProjectById() {
        // Arrange
        Project savedProject = projectRepository.save(testProject);

        // Act
        projectRepository.deleteById(savedProject.getId());

        // Assert
        assertFalse(projectRepository.existsById(savedProject.getId()));
    }
}