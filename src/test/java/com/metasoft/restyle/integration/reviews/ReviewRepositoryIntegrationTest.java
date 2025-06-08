package com.metasoft.restyle.integration.reviews;

import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ContractorId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ProjectId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.Rating;
import com.metasoft.restyle.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

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
public class ReviewRepositoryIntegrationTest {

    @Autowired
    private ReviewRepository reviewRepository;

    private Review testReview;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();

        // Create a test review
        testReview = new Review(
                1,
                2,
                "3 months",
                4,
                "Repository test review",
                "test-image.jpg"
        );
    }

    @Test
    void shouldSaveAndRetrieveReview() {
        // Act
        Review savedReview = reviewRepository.save(testReview);
        Optional<Review> retrievedReview = reviewRepository.findById(savedReview.getId());

        // Assert
        assertTrue(retrievedReview.isPresent());
        assertEquals(savedReview.getId(), retrievedReview.get().getId());
        assertEquals(1, retrievedReview.get().getContractorId());
        assertEquals(2, retrievedReview.get().getProjectId());
        assertEquals(4, retrievedReview.get().getRating());
        assertEquals("Repository test review", retrievedReview.get().getComment());
    }

    @Test
    void shouldFindReviewByContractorIdAndProjectId() {
        // Arrange
        reviewRepository.save(testReview);
        ContractorId contractorId = new ContractorId(1);
        ProjectId projectId = new ProjectId(2);

        // Act
        Optional<Review> result = reviewRepository.findByContractorIdAndProjectId(contractorId, projectId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getContractorId());
        assertEquals(2, result.get().getProjectId());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentContractorIdAndProjectId() {
        // Arrange
        ContractorId nonExistentContractorId = new ContractorId(999);
        ProjectId nonExistentProjectId = new ProjectId(999);

        // Act
        Optional<Review> result = reviewRepository.findByContractorIdAndProjectId(nonExistentContractorId, nonExistentProjectId);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldFindReviewsByRating() {
        // Arrange
        reviewRepository.save(testReview);
        reviewRepository.save(new Review(2, 3, "2 months", 5, "Excellent service", "image2.jpg"));
        reviewRepository.save(new Review(3, 4, "1 month", 4, "Good work", "image3.jpg"));

        // Act
        boolean exists = reviewRepository.existsByRating(new Rating(4));

        // Assert
        assertTrue(exists);
    }

    @Test
    void shouldDeleteReview() {
        // Arrange
        Review savedReview = reviewRepository.save(testReview);

        // Act
        reviewRepository.delete(savedReview);

        // Assert
        assertTrue(reviewRepository.findById(savedReview.getId()).isEmpty());
    }

    @Test
    void shouldUpdateReview() {
        // Arrange
        Review savedReview = reviewRepository.save(testReview);

        // Act
        savedReview.updateComment("Updated comment");
        savedReview.updateDuration("5 months");
        savedReview.updateImage("updated-image.jpg");
        reviewRepository.save(savedReview);

        // Assert
        Review updatedReview = reviewRepository.findById(savedReview.getId()).orElseThrow();
        assertEquals("Updated comment", updatedReview.getComment());
        assertEquals("5 months", updatedReview.getDuration());
        assertEquals("updated-image.jpg", updatedReview.getImage());
    }
}