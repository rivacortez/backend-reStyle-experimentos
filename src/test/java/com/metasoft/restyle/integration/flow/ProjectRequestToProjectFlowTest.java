package com.metasoft.restyle.integration.flow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.iam.domain.model.aggregates.User;
import com.metasoft.restyle.platform.iam.domain.model.valueobjects.Roles;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.RoleRepository;
import com.metasoft.restyle.platform.iam.infrastructure.persistence.jpa.repositories.UserRepository;
import com.metasoft.restyle.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import com.metasoft.restyle.platform.project.infrastructure.persistance.jpa.ProjectRepository;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.CreateProjectResource;
import com.metasoft.restyle.platform.project.interfaces.rest.resources.ProjectResource;
import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.CreateReviewResource;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.ReviewResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectRequestToProjectFlowTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BearerTokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private String authToken;

    @BeforeEach
    void setup() {
        if (!userRepository.existsByUsername("test-user")) {
            // First save the user without roles
            var testUser = new User();
            testUser.setUsername("test-user");
            testUser.setPassword(passwordEncoder.encode("password"));
            testUser.setEmail("test-user@example.com");
            testUser.setFirstName("Test");
            testUser.setPaternalSurname("User");
            testUser.setMaternalSurname("Test");

            // Save user first
            User savedUser = userRepository.save(testUser);

            // Then get the role and associate it with the saved user
            var userRole = roleRepository.findByName(Roles.ROLE_CONTRACTOR)
                    .orElseThrow(() -> new RuntimeException("Required role not found"));

            // Now associate the role with the saved user
            savedUser.setRoles(Set.of(userRole));
            userRepository.save(savedUser);
        }

        // Create authentication token for testing
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "test-user",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        // Generate JWT token
        authToken = tokenService.generateToken(authentication);
    }

    @AfterEach
    void cleanup() {
        reviewRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void testProjectToReviewFlow() throws Exception {
        // Step 1: Create a project
        CreateProjectResource projectResource = new CreateProjectResource(
                "Bathroom Remodel", // name
                "Complete bathroom renovation project", // description
                1, // businessId
                2, // contractorId
                new Date(), // startDate (today)
                new Date(System.currentTimeMillis() + 2592000000L), // finishDate (30 days from now)
                "https://example.com/bathroom-remodel.jpg" // image
        );

        // Submit the project creation request with authorization header
        MvcResult projectResult = mockMvc.perform(post("/api/v1/projects")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectResource)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract the created project from the response
        ProjectResource createdProject = objectMapper.readValue(
                projectResult.getResponse().getContentAsString(),
                ProjectResource.class
        );

        // Verify the project was created in the database
        assertTrue(projectRepository.existsById(createdProject.id()));

        // Step 2: Create a review for the project
        CreateReviewResource reviewResource = new CreateReviewResource(
                2, // contractorId
                createdProject.id().intValue(), // projectId
                "2 weeks", // duration
                5, // rating
                "Excellent work, very professional and completed on time", // comment
                "https://example.com/review-image.jpg" // image
        );

        // Submit the review creation request with authorization header
        MvcResult reviewResult = mockMvc.perform(post("/api/v1/reviews")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewResource)))
                .andExpect(status().isCreated())
                .andReturn();

        // Extract the created review from the response
        ReviewResource createdReview = objectMapper.readValue(
                reviewResult.getResponse().getContentAsString(),
                ReviewResource.class
        );

        // Verify the review was created
        assertNotNull(createdReview.id());

        // Fetch the review from the database and verify its properties
        Review savedReview = reviewRepository.findById(createdReview.id())
                .orElseThrow(() -> new AssertionError("Review not found in database"));

        assertEquals(reviewResource.contractorId(), savedReview.getContractorId());
        assertEquals(reviewResource.projectId(), savedReview.getProjectId());
        assertEquals(reviewResource.duration(), savedReview.getDuration());
        assertEquals(reviewResource.rating(), savedReview.getRating());
        assertEquals(reviewResource.comment(), savedReview.getComment());
        assertEquals(reviewResource.image(), savedReview.getImage());
    }
}