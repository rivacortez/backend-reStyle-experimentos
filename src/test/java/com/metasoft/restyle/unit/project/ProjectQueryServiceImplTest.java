package com.metasoft.restyle.unit.project;

import com.metasoft.restyle.platform.project.application.internal.queryservices.ProjectQueryServiceImpl;
import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.model.commands.CreateProjectCommand;
import com.metasoft.restyle.platform.project.domain.model.queries.GetAllProjects;
import com.metasoft.restyle.platform.project.domain.model.queries.GetAllProjectsByBusinessIdQuery;
import com.metasoft.restyle.platform.project.domain.model.queries.GetProjectByIdQuery;
import com.metasoft.restyle.platform.project.infrastructure.persistance.jpa.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

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
public class ProjectQueryServiceImplTest {

    @Autowired
    private ProjectQueryServiceImpl projectQueryService;

    @Autowired
    private ProjectRepository projectRepository;

    private Project project1;
    private Project project2;
    private Project project3;
    private Date startDate;
    private Date finishDate;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();

        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 3);
        finishDate = calendar.getTime();

        // Create test projects
        project1 = projectRepository.save(new Project(new CreateProjectCommand(
                "Project 1", "https://example.com/image1.jpg", "Description 1", 1, 1, startDate, finishDate
        )));

        project2 = projectRepository.save(new Project(new CreateProjectCommand(
                "Project 2", "https://example.com/image2.jpg", "Description 2", 1, 2, startDate, finishDate
        )));

        project3 = projectRepository.save(new Project(new CreateProjectCommand(
                "Project 3", "https://example.com/image3.jpg", "Description 3", 2, 3, startDate, finishDate
        )));
    }

    @Test
    void shouldReturnAllProjects() {
        // Act
        List<Project> projects = projectQueryService.handle(new GetAllProjects());

        // Assert
        assertEquals(3, projects.size());
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(project1.getId())));
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(project2.getId())));
        assertTrue(projects.stream().anyMatch(p -> p.getId().equals(project3.getId())));
    }

    @Test
    void shouldReturnProjectsByBusinessId() {
        // Act
        List<Project> businessProjects = projectQueryService.handle(new GetAllProjectsByBusinessIdQuery(1L));

        // Assert
        assertEquals(2, businessProjects.size());
        assertTrue(businessProjects.stream().anyMatch(p -> p.getId().equals(project1.getId())));
        assertTrue(businessProjects.stream().anyMatch(p -> p.getId().equals(project2.getId())));
        assertFalse(businessProjects.stream().anyMatch(p -> p.getId().equals(project3.getId())));
    }

    @Test
    void shouldReturnEmptyListForNonExistentBusinessId() {
        // Act
        List<Project> businessProjects = projectQueryService.handle(new GetAllProjectsByBusinessIdQuery(999L));

        // Assert
        assertTrue(businessProjects.isEmpty());
    }

    @Test
    void shouldReturnProjectById() {
        // Act
        Optional<Project> result = projectQueryService.handle(new GetProjectByIdQuery(project1.getId()));

        // Assert
        assertTrue(result.isPresent());
        assertEquals(project1.getId(), result.get().getId());
        assertEquals("Project 1", result.get().getName());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentId() {
        // Act
        Optional<Project> result = projectQueryService.handle(new GetProjectByIdQuery(999L));

        // Assert
        assertTrue(result.isEmpty());
    }
}