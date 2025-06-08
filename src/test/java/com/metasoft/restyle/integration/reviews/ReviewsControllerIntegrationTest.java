package com.metasoft.restyle.integration.reviews;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.CreateReviewResource;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.UpdateReviewResource;
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

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ReviewsControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();
    }

    @Test
    void shouldCreateReview() throws Exception {
        // Arrange
        CreateReviewResource resource = new CreateReviewResource(
                1, 2, "2 months", 5, "Great contractor!", "http://example.com/image.jpg"
        );

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.contractorId", is(1)))
                .andExpect(jsonPath("$.projectId", is(2)))
                .andExpect(jsonPath("$.rating", is(5)))
                .andExpect(jsonPath("$.comment", is("Great contractor!")))
                .andExpect(jsonPath("$.image", is("http://example.com/image.jpg")));

        // Verify the review was actually saved
        assertEquals(1, reviewRepository.count());
    }

    @Test
    void shouldGetAllReviews() throws Exception {
        // Arrange - create two reviews
        reviewRepository.save(new Review(1, 1, "2 months", 5, "Excellent work", "image1.jpg"));
        reviewRepository.save(new Review(2, 2, "3 months", 4, "Good service", "image2.jpg"));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/reviews")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].contractorId", is(1)))
                .andExpect(jsonPath("$[1].contractorId", is(2)));
    }

    @Test
    void shouldUpdateReview() throws Exception {
        // Arrange - create a review to update
        Review savedReview = reviewRepository.save(
                new Review(1, 2, "3 months", 4, "Initial comment", "old-image.jpg")
        );

        UpdateReviewResource updateResource = new UpdateReviewResource(
                "6 months", "Updated comment", "new-image.jpg"
        );

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/reviews/{id}", savedReview.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateResource)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedReview.getId().intValue())))
                .andExpect(jsonPath("$.duration", is("6 months")))
                .andExpect(jsonPath("$.comment", is("Updated comment")))
                .andExpect(jsonPath("$.image", is("new-image.jpg")));

        // Verify the changes in the database
        Review updatedReview = reviewRepository.findById(savedReview.getId()).orElseThrow();
        assertEquals("6 months", updatedReview.getDuration());
        assertEquals("Updated comment", updatedReview.getComment());
        assertEquals("new-image.jpg", updatedReview.getImage());
    }

    @Test
    void shouldDeleteReview() throws Exception {
        // Arrange - create a review to delete
        Review savedReview = reviewRepository.save(
                new Review(1, 2, "3 months", 4, "Test comment", "test-image.jpg")
        );

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/v1/reviews/{id}", savedReview.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Review deleted successfully")));

        // Verify the review was deleted
        assertTrue(reviewRepository.findById(savedReview.getId()).isEmpty());
    }
/*
    @Test
    void shouldReturnNotFoundForNonExistentReview() throws Exception {
        // Arrange
        UpdateReviewResource updateResource = new UpdateReviewResource(
                "6 months", "Updated comment", "new-image.jpg"
        );

        // Act & Assert - Try to update a non-existent review
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/reviews/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateResource)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectInvalidRating() throws Exception {
        // Arrange - invalid rating (outside 1-5 range)
        CreateReviewResource invalidResource = new CreateReviewResource(
                1, 2, "2 months", 10, "Great contractor!", "http://example.com/image.jpg"
        );

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidResource)))
                .andExpect(status().isBadRequest());
    }*/
}