package com.metasoft.restyle.integration.business;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.business.infrastructure.persistance.jpa.BusinessRepository;
import com.metasoft.restyle.platform.business.interfaces.rest.resources.CreateBusinessResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BusinessesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BusinessRepository businessRepository;

    @BeforeEach
    void setUp() {
        businessRepository.deleteAll();
    }

    @Test
    void createBusinessReturnsCreatedBusiness() throws Exception {
        // Arrange
        CreateBusinessResource resource = new CreateBusinessResource(
                "Integration Test Business",
                "Test Description",
                "123 Test Street",
                "Test City",
                "test-logo.png",
                "Testing Services",
                1);

        String requestBody = objectMapper.writeValueAsString(resource);

        // Act & Assert
        mockMvc.perform(post("/api/v1/businesses")
                        .with(user("testuser").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Integration Test Business"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.address").value("123 Test Street"))
                .andExpect(jsonPath("$.city").value("Test City"))
                .andExpect(jsonPath("$.image").value("test-logo.png"))
                .andExpect(jsonPath("$.expertise").value("Testing Services"))
                .andExpect(jsonPath("$.remodelerId").value(1));

        // Verify database state
        assertEquals(1, businessRepository.count());
    }

    @Test
    void getBusinessByIdReturnsCorrectBusiness() throws Exception {
        // Arrange
        CreateBusinessResource resource = new CreateBusinessResource(
                "Business By ID Test",
                "Find me by ID",
                "456 ID Street",
                "ID City",
                "id-test.jpg",
                "ID Testing",
                2);

        String requestBody = objectMapper.writeValueAsString(resource);

        // Create business first
        String responseBody = mockMvc.perform(post("/api/v1/businesses")
                        .with(user("testuser").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(responseBody).get("id").asLong();

        // Act & Assert - Get by ID
        mockMvc.perform(get("/api/v1/businesses/{id}", id)
                        .with(user("testuser").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Business By ID Test"))
                .andExpect(jsonPath("$.description").value("Find me by ID"));
    }

    @Test
    void getNonExistingBusinessReturns404() throws Exception {
        mockMvc.perform(get("/api/v1/businesses/999")
                        .with(user("testuser").roles("USER", "ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllBusinessesReturnsAllBusinesses() throws Exception {
        // Arrange
        createTestBusiness("First Business", "First Description");
        createTestBusiness("Second Business", "Second Description");
        createTestBusiness("Third Business", "Third Description");

        // Act & Assert
        mockMvc.perform(get("/api/v1/businesses")
                        .with(user("testuser").roles("USER", "ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                        "First Business", "Second Business", "Third Business")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder(
                        "First Description", "Second Description", "Third Description")));
    }
 /*
    @Test
    void createBusinessWithDuplicateNameReturnsBadRequest() throws Exception {
        // Arrange
        CreateBusinessResource resource = new CreateBusinessResource(
                "Duplicate Business",
                "Original Business",
                "123 Duplicate St",
                "Duplicate City",
                "duplicate.jpg",
                "Duplicate Testing",
                3);

        String requestBody = objectMapper.writeValueAsString(resource);

        // Create first business
        mockMvc.perform(post("/api/v1/businesses")
                        .with(user("testuser").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());

        // Try to create duplicate
        mockMvc.perform(post("/api/v1/businesses")
                        .with(user("testuser").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
 */
    private void createTestBusiness(String name, String description) throws Exception {
        CreateBusinessResource resource = new CreateBusinessResource(
                name,
                description,
                "123 Test Street",
                "Test City",
                "test.jpg",
                "Test Expertise",
                1);

        String requestBody = objectMapper.writeValueAsString(resource);

        mockMvc.perform(post("/api/v1/businesses")
                        .with(user("testuser").roles("USER", "ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }
}