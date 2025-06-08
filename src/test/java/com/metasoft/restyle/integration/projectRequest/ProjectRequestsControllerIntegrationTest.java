package com.metasoft.restyle.integration.projectRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.projectRequest.domain.model.aggregates.ProjectRequest;
import com.metasoft.restyle.platform.projectRequest.domain.services.ProjectRequestCommandService;
import com.metasoft.restyle.platform.projectRequest.domain.services.ProjectRequestQueryService;
import com.metasoft.restyle.platform.projectRequest.interfaces.rest.resources.CreateProjectRequestResource;
import com.metasoft.restyle.platform.projectRequest.infrastructure.persistance.jpa.ProjectRequestRepository;
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
public class ProjectRequestsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProjectRequestRepository projectRequestRepository;

    private CreateProjectRequestResource validRequestResource;
    private Date deadlineDate;

    @BeforeEach
    void setUp() {
        projectRequestRepository.deleteAll();

        // Setup deadline date
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 2);
        deadlineDate = calendar.getTime();

        // Create valid project request resource
        validRequestResource = new CreateProjectRequestResource(
                "API Test Request",
                "Smith",
                "john.smith@example.com",
                "123456789",
                "123 Test St",
                "Test City",
                "Test renovation project",
                1,
                2,
                deadlineDate,
                3,
                5000
        );
    }

    @Test
    void shouldCreateProjectRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestResource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(notNullValue())))
                .andExpect(jsonPath("$.name", is("API Test Request")))
                .andExpect(jsonPath("$.surname", is("Smith")))
                .andExpect(jsonPath("$.email", is("john.smith@example.com")));
    }

    @Test
    void shouldGetProjectRequestById() throws Exception {
        // Arrange - Create a project request to retrieve later
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestResource)))
                .andExpect(status().isCreated());

        // Get the ID of the first project request (assuming ID = 1)
        Long requestId = 1L;

        // Act & Assert - Retrieve the project request
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/project-requests/{id}", requestId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestId.intValue())))
                .andExpect(jsonPath("$.name", is("API Test Request")));
    }

    @Test
    void shouldGetProjectRequestsByBusinessId() throws Exception {
        // Arrange - Create two requests for the same business
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestResource)))
                .andExpect(status().isCreated());

        CreateProjectRequestResource secondRequest = new CreateProjectRequestResource(
                "Second Business Request",
                "Johnson",
                "jane.johnson@example.com",
                "987654321",
                "456 Other St",
                "Other City",
                "Another project for business 1",
                1, // Same business ID
                3,
                deadlineDate,
                2,
                4000
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isCreated());

        // Act & Assert - Search by business ID
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/project-requests")
                        .param("businessId", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].businessId", everyItem(is(1))));
    }

    @Test
    void shouldGetProjectRequestsByContractorId() throws Exception {
        // Arrange - Create two requests for the same contractor
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestResource)))
                .andExpect(status().isCreated());

        CreateProjectRequestResource secondRequest = new CreateProjectRequestResource(
                "Second Contractor Request",
                "Wilson",
                "tom.wilson@example.com",
                "555123456",
                "789 Third St",
                "Third City",
                "Another project for contractor 2",
                3,
                2, // Same contractor ID
                deadlineDate,
                1,
                3000
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(secondRequest)))
                .andExpect(status().isCreated());

        // Act & Assert - Search by contractor ID
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/project-requests")
                        .param("contractorId", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].contractorId", everyItem(is(2))));
    }

    @Test
    void shouldReturnNotFoundForNonExistentProjectRequest() throws Exception {
        // Act & Assert - Try to get non-existent project request
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/project-requests/{id}", 999)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidProjectRequest() throws Exception {
        // Create invalid JSON with missing name
        String invalidJson = "{\"surname\":\"Smith\",\"email\":\"john@example.com\",\"phone\":\"123456789\"," +
                "\"address\":\"123 Test St\",\"city\":\"Test City\",\"summary\":\"Test summary\"," +
                "\"businessId\":1,\"contractorId\":2,\"budget\":5000,\"rooms\":3," +
                "\"deadlineDate\":\"" + objectMapper.writeValueAsString(deadlineDate).replace("\"", "") + "\"}";

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/project-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}