package com.metasoft.restyle.unit.reviews;

import com.metasoft.restyle.platform.reviews.application.internal.commandservices.ReviewCommandServiceImpl;
import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.metasoft.restyle.platform.reviews.domain.model.commands.DeleteReviewCommand;
import com.metasoft.restyle.platform.reviews.domain.model.commands.UpdateReviewCommand;
import com.metasoft.restyle.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ReviewCommandServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewCommandServiceImpl reviewCommandService;

    private Review testReview;
    private final Long REVIEW_ID = 1L;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testReview = new Review(1, 2, "3 months", 4, "Great work", "image-url.jpg");

        // Use reflection to set the ID since it's normally generated
        try {
            var field = Review.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(testReview, REVIEW_ID);
        } catch (Exception e) {
            fail("Failed to set review ID");
        }
    }

    @Test
    void shouldCreateReview() {
        // Arrange
        CreateReviewCommand command = new CreateReviewCommand(
                1, 2, "3 months", 5, "Excellent service", "image-url.jpg"
        );
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> {
            Review savedReview = invocation.getArgument(0);
            // Set ID on the saved review
            try {
                var field = Review.class.getDeclaredField("id");
                field.setAccessible(true);
                field.set(savedReview, REVIEW_ID);
            } catch (Exception e) {
                fail("Failed to set review ID");
            }
            return savedReview;
        });

        // Act
        Long result = reviewCommandService.handle(command);

        // Assert
        assertEquals(REVIEW_ID, result);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    /*
    @Test
    void shouldUpdateReview() {
        // Arrange
        UpdateReviewCommand command = new UpdateReviewCommand(
                REVIEW_ID, "6 months", "Updated comment", "new-image-url.jpg"
        );
        when(reviewRepository.findById(REVIEW_ID)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        verify(reviewRepository, times(1)).findById(REVIEW_ID);
        verify(reviewRepository, times(1)).save(testReview);

        // Verify the review was updated with the correct values
        assertEquals("6 months", testReview.getDuration());
        assertEquals("Updated comment", testReview.getComment());
        assertEquals("new-image-url.jpg", testReview.getImage());
    }
*/
    @Test
    void shouldThrowExceptionWhenUpdateNonExistentReview() {
        // Arrange
        UpdateReviewCommand command = new UpdateReviewCommand(
                999L, "6 months", "Updated comment", "new-image-url.jpg"
        );
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewCommandService.handle(command);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void shouldDeleteReview() {
        // Arrange
        DeleteReviewCommand command = new DeleteReviewCommand(REVIEW_ID);
        when(reviewRepository.findById(REVIEW_ID)).thenReturn(Optional.of(testReview));

        // Act
        Optional<Review> result = reviewCommandService.handle(command);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(REVIEW_ID, result.get().getId());
        verify(reviewRepository, times(1)).findById(REVIEW_ID);
        verify(reviewRepository, times(1)).delete(testReview);
    }

    @Test
    void shouldThrowExceptionWhenDeleteNonExistentReview() {
        // Arrange
        DeleteReviewCommand command = new DeleteReviewCommand(999L);
        when(reviewRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            reviewCommandService.handle(command);
        });

        assertEquals("Review not found", exception.getMessage());
        verify(reviewRepository, never()).delete(any(Review.class));
    }
}