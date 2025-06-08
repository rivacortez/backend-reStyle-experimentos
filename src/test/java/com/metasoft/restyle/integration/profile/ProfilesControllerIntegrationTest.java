package com.metasoft.restyle.integration.profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.profiles.domain.model.aggregates.Profile;
import com.metasoft.restyle.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import com.metasoft.restyle.platform.profiles.interfaces.rest.resources.CreateProfileResource;
import com.metasoft.restyle.platform.profiles.interfaces.rest.resources.ProfileResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = {"USER", "ADMIN"}) // Add this annotation
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class ProfilesControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        profileRepository.deleteAll();
    }

    @Test
    void shouldCreateProfile() throws Exception {
        // Arrange
        CreateProfileResource resource = new CreateProfileResource(
                "integration@example.com",
                "Password123",
                "REMODELER",
                "Integration",
                "Test",
                "User"
        );
        String requestBody = objectMapper.writeValueAsString(resource);

        // Act & Assert
        MvcResult result = mockMvc.perform(post("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("integration@example.com"))
                .andExpect(jsonPath("$.fullName").value("Integration Test User"))
                .andReturn();

        // Extract the created profile ID
        ProfileResource createdProfile = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                ProfileResource.class
        );

        // Verify profile exists in database
        assertTrue(profileRepository.findById(createdProfile.id()).isPresent());
    }

    @Test
    void shouldGetProfileById() throws Exception {
        // Arrange - Create a profile first
        Profile profile = new Profile(
                "get@example.com",
                "Password123",
                "CONTRACTOR",
                "Get",     
                "Test",    
                "Profile"
        );
        profile = profileRepository.save(profile);

        // Act & Assert
        mockMvc.perform(get("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/profiles/{profileId}", profile.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(profile.getId()))
                .andExpect(jsonPath("$.email").value("get@example.com"))
                .andExpect(jsonPath("$.fullName").value("Get Profile Test"));
    }

    @Test
    void shouldGetAllProfiles() throws Exception {
        // Arrange - Create multiple profiles
        profileRepository.save(new Profile(
                "user1@example.com",
                "Password123",
                "REMODELER",
                "User",
                "One",
                "Test"
        ));
        profileRepository.save(new Profile(
                "user2@example.com",
                "Password456",
                "CONTRACTOR",
                "User",
                "Two",
                "Test"
        ));

        // Act & Assert
        mockMvc.perform(get("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[1].email").exists());
    }
/*
    @Test
    void shouldReturnBadRequestForDuplicateEmail() throws Exception {
        // Arrange - Create a profile first
        profileRepository.save(new Profile(
                "duplicate@example.com",
                "Password123",
                "REMODELER",
                "Original",
                "User",
                "Test"
        ));

        // Try to create another profile with the same email
        CreateProfileResource resource = new CreateProfileResource(
                "duplicate@example.com",
                "DifferentPassword",
                "CONTRACTOR",
                "Duplicate",
                "Email",
                "Test"
        );
        String requestBody = objectMapper.writeValueAsString(resource);

        // Act & Assert
        mockMvc.perform(post("https://restyle-web-services-cyf0axfvakcxaehd.brazilsouth-01.azurewebsites.net/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }
    */
}