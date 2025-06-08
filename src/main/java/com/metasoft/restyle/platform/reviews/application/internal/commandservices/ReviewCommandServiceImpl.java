package com.metasoft.restyle.platform.reviews.application.internal.commandservices;

import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.domain.model.commands.CreateReviewCommand;
import com.metasoft.restyle.platform.reviews.domain.model.commands.DeleteReviewCommand;
import com.metasoft.restyle.platform.reviews.domain.model.commands.UpdateReviewCommand;
import com.metasoft.restyle.platform.reviews.domain.services.ReviewCommandService;
import com.metasoft.restyle.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReviewCommandServiceImpl implements ReviewCommandService {

    private final ReviewRepository reviewRepository;

    public ReviewCommandServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Long handle(CreateReviewCommand command) {
        var review = new Review(command.contractorId(), command.projectId(), command.duration(), command.rating(), command.comment(), command.image());
        reviewRepository.save(review);
        return review.getId();
    }

    @Override
    public Optional<Review> handle(UpdateReviewCommand command) {
        var review = reviewRepository.findById(command.reviewId())
                .orElseThrow(()-> new IllegalArgumentException("Review not found"));
        review.updateDuration(command.duration());
        review.updateComment(command.comment());
        review.updateImage(command.image());

        reviewRepository.save(review);
        return reviewRepository.findById(command.reviewId());
    }

    @Override
    public Optional<Review> handle(DeleteReviewCommand command) {
        var review = reviewRepository.findById(command.reviewId())
                .orElseThrow(()-> new IllegalArgumentException("Review not found"));
        reviewRepository.delete(review);
        return Optional.of(review);
    }
}
