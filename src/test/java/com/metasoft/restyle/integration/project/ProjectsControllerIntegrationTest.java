package com.metasoft.restyle.integration.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.project.domain.model.aggregates.Project;
import com.metasoft.restyle.platform.project.domain.services.ProjectCommandService;
import com.metasoft.restyle.platform.project.domain.services.ProjectQueryService;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.CreateProjectResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"})
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ProjectsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectCommandService projectCommandService;

    @Autowired
    private ProjectQueryService projectQueryService;

    private CreateProjectResource validProjectResource;
    private Date startDate;
    private Date finishDate;

    @BeforeEach
    void setUp() {
        // Setup dates for testing
        Calendar calendar = Calendar.getInstance();
        startDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 3);
        finishDate = calendar.getTime();

        // Create valid project resource
        validProjectResource = new CreateProjectResource(
                "API Test Project",
                "API test description",
                1,
                2,
                startDate,
                finishDate,
                "https://example.com/api-test.jpg"
        );
    }

    @Test
    void shouldCreateProject() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProjectResource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.name", is("API Test Project")))
                .andExpect(jsonPath("$.description", is("API test description")));
    }

    @Test
    void shouldRejectInvalidProject() throws Exception {
        // The CreateProjectResource constructor throws an exception for null fields
        // So we need to test this differently - by creating a map with missing name
        String invalidJson = "{\"description\":\"Description\",\"businessId\":1,\"contractorId\":2," +
                "\"startDate\":\"" + objectMapper.writeValueAsString(startDate).replace("\"", "") + "\"," +
                "\"finishDate\":\"" + objectMapper.writeValueAsString(finishDate).replace("\"", "") + "\"," +
                "\"image\":\"https://example.com/image.jpg\"}";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetProjectById() throws Exception {
        // Arrange - Create a project to retrieve later
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProjectResource)))
                .andExpect(status().isCreated())
                .andReturn();

        // Get the ID of the first project
        Long projectId = 1L;

        // Act & Assert - Retrieve the project
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/projects/{id}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(projectId.intValue())))
                .andExpect(jsonPath("$.name", is("API Test Project")));
    }

    @Test
    void shouldGetProjectsByBusinessId() throws Exception {
        // Arrange - Create two projects for the same business
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validProjectResource)))
                .andExpect(status().isCreated());

        CreateProjectResource secondProject = new CreateProjectResource(
                "Second Business Project",
                "Another project for business 1",
                1, // Same business ID
                3,
                startDate,
                finishDate,
                "https://example.com/second.jpg"
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondProject)))
                .andExpect(status().isCreated());

        // Act & Assert - Search by business ID
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/projects/search")
                        .param("businessId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].businessId", everyItem(is(1))));
    }
}