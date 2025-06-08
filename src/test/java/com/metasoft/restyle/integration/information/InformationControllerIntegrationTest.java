package com.metasoft.restyle.integration.information;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Contractor;
import com.metasoft.restyle.platform.information.profiles.domain.model.aggregates.Remodeler;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.ContractorRepository;
import com.metasoft.restyle.platform.information.profiles.infrastructure.persistence.jpa.repositories.RemodelerRepository;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.resources.CreateContractorResource;
import com.metasoft.restyle.platform.information.profiles.interfaces.rest.resources.CreateRemodelerResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = {"USER", "ADMIN"}) // Add mock authentication for all tests
public class InformationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ContractorRepository contractorRepository;

    @Autowired
    private RemodelerRepository remodelerRepository;

    @BeforeEach
    void setUp() {
        contractorRepository.deleteAll();
        remodelerRepository.deleteAll();
    }

    @Test
    void shouldCreateContractor() throws Exception {
        // Arrange
        CreateContractorResource resource = new CreateContractorResource(
                "Integration test contractor",
                "+1234567890"
        );
        String requestBody = objectMapper.writeValueAsString(resource);

        // Act & Assert
        mockMvc.perform(post("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/Contractors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Integration test contractor"))
                .andExpect(jsonPath("$.phone").value("+1234567890"));

        // Verify contractor exists in database
        assertTrue(contractorRepository.findByPhone("+1234567890").isPresent());
    }


    @Test
    void shouldGetContractorById() throws Exception {
        // Arrange - Create a contractor first
        Contractor contractor = new Contractor("Get by ID contractor", "+9876543210");
        contractor = contractorRepository.save(contractor);

        // Act & Assert
        mockMvc.perform(get("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/Contractors/{contractorId}", contractor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(contractor.getId()))
                .andExpect(jsonPath("$.description").value("Get by ID contractor"))
                .andExpect(jsonPath("$.phone").value("+9876543210"));
    }

    @Test
    void shouldGetAllContractors() throws Exception {
        // Arrange - Create multiple contractors
        contractorRepository.save(new Contractor("First test contractor", "+1111111111"));
        contractorRepository.save(new Contractor("Second test contractor", "+2222222222"));

        // Act & Assert
        mockMvc.perform(get("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/Contractors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].description").exists())
                .andExpect(jsonPath("$[1].description").exists());
    }

    @Test
    void shouldCreateRemodeler() throws Exception {
        // Arrange
        CreateRemodelerResource resource = new CreateRemodelerResource(
                "Integrationtestremodeler",
                "+5556667777",
                "PREMIUM"
        );
        String requestBody = objectMapper.writeValueAsString(resource);

        // Act & Assert
        mockMvc.perform(post("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/remodelers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Integrationtestremodeler"))
                .andExpect(jsonPath("$.phone").value("+5556667777"))
                .andExpect(jsonPath("$.subscriptionType").value("PREMIUM"));

        // Verify remodeler exists in database
        assertTrue(remodelerRepository.findByPhone("+5556667777").isPresent());
    }

    @Test
    void shouldGetRemodelerById() throws Exception {
        // Arrange - Create a remodeler first
        Remodeler remodeler = new Remodeler("Get by ID remodeler", "+7778889999", "BASIC");
        remodeler = remodelerRepository.save(remodeler);

        // Act & Assert
        mockMvc.perform(get("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/remodelers/{remodelerId}", remodeler.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(remodeler.getId()))
                .andExpect(jsonPath("$.description").value("Get by ID remodeler"))
                .andExpect(jsonPath("$.phone").value("+7778889999"))
                .andExpect(jsonPath("$.subscriptionType").value("BASIC"));
    }
}