package com.metasoft.restyle.unit.reviews;

import com.metasoft.restyle.platform.reviews.application.internal.queryservices.ReviewQueryServiceImpl;
import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetAllReviewsQuery;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetReviewByContractorIdAndProjectId;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetReviewByIdQuery;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ContractorId;
import com.metasoft.restyle.platform.reviews.domain.model.valueobjects.ProjectId;
import com.metasoft.restyle.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class ReviewQueryServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewQueryServiceImpl reviewQueryService;

    private Review testReview1;
    private Review testReview2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testReview1 = new Review(1, 1, "2 months", 5, "Excellent work", "image-url-1.jpg");
        testReview2 = new Review(2, 2, "3 months", 4, "Good service", "image-url-2.jpg");

        // Use reflection to set the IDs
        try {
            var field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(testReview1, 1L);
            field.set(testReview2, 2L);
        } catch (Exception e) {
            fail("Failed to set review IDs");
        }
    }

    @Test
    void shouldReturnReviewById() {
        // Arrange
        Long reviewId = 1L;
        GetReviewByIdQuery query = new GetReviewByIdQuery(reviewId);
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(testReview1));

        // Act
        Optional<Review> result = reviewQueryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(reviewId, result.get().getId());
        assertEquals("Excellent work", result.get().getComment());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentId() {
        // Arrange
        Long nonExistentId = 999L;
        GetReviewByIdQuery query = new GetReviewByIdQuery(nonExistentId);
        when(reviewRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        Optional<Review> result = reviewQueryService.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnReviewByContractorIdAndProjectId() {
        // Arrange
        ContractorId contractorId = new ContractorId(1);
        ProjectId projectId = new ProjectId(1);
        GetReviewByContractorIdAndProjectId query = new GetReviewByContractorIdAndProjectId(contractorId, projectId);
        when(reviewRepository.findByContractorIdAndProjectId(contractorId, projectId))
                .thenReturn(Optional.of(testReview1));

        // Act
        Optional<Review> result = reviewQueryService.handle(query);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1, result.get().getContractorId());
        assertEquals(1, result.get().getProjectId());
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentContractorAndProject() {
        // Arrange
        ContractorId contractorId = new ContractorId(999);
        ProjectId projectId = new ProjectId(999);
        GetReviewByContractorIdAndProjectId query = new GetReviewByContractorIdAndProjectId(contractorId, projectId);
        when(reviewRepository.findByContractorIdAndProjectId(contractorId, projectId))
                .thenReturn(Optional.empty());

        // Act
        Optional<Review> result = reviewQueryService.handle(query);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnAllReviews() {
        // Arrange
        GetAllReviewsQuery query = new GetAllReviewsQuery();
        List<Review> expectedReviews = Arrays.asList(testReview1, testReview2);
        when(reviewRepository.findAll()).thenReturn(expectedReviews);

        // Act
        List<Review> result = reviewQueryService.handle(query);

        // Assert
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }
}