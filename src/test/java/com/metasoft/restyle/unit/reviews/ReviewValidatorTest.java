package com.metasoft.restyle.unit.reviews;

import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ContractorId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ProjectId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.Rating;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class ReviewValidatorTest {

    @Test
    void shouldCreateValidReview() {
        // Act & Assert - no exception should be thrown
        Review review = new Review(
                1,
                2,
                "3 months",
                5,
                "Great service",
                "image-url.jpg"
        );

        assertNotNull(review);
        assertEquals(1, review.getContractorId());
        assertEquals(2, review.getProjectId());
        assertEquals(5, review.getRating());
        assertEquals("3 months", review.getDuration());
        assertEquals("Great service", review.getComment());
        assertEquals("image-url.jpg", review.getImage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void shouldRejectInvalidContractorId(int invalidId) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ContractorId(invalidId);
        });

        assertEquals("ContractorId must be greater than or equal to 1", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -10})
    void shouldRejectInvalidProjectId(int invalidId) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new ProjectId(invalidId);
        });

        assertEquals("ProjectId must be greater than or equal to 1", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, 6, 10})
    void shouldRejectInvalidRating(int invalidRating) {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Rating(invalidRating);
        });

        assertEquals("Rating must be between 1 and 5", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void shouldAcceptValidRating(int validRating) {
        // Act & Assert - no exception should be thrown
        Rating rating = new Rating(validRating);
        assertEquals(validRating, rating.getRating());
    }

    @Test
    void shouldUpdateReviewFields() {
        // Arrange
        Review review = new Review(
                1,
                2,
                "3 months",
                5,
                "Initial comment",
                "initial-image.jpg"
        );

        // Act
        review.updateDuration("5 months");
        review.updateComment("Updated comment");
        review.updateImage("updated-image.jpg");

        // Assert
        assertEquals("5 months", review.getDuration());
        assertEquals("Updated comment", review.getComment());
        assertEquals("updated-image.jpg", review.getImage());
    }
}