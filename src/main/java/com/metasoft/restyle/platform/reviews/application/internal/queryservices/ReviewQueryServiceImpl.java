package com.metasoft.restyle.platform.reviews.application.internal.queryservices;

import com.metasoft.restyle.platform.reviews.domain.model.aggregates.Review;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetAllReviewsQuery;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetReviewByContractorIdAndProjectId;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetReviewByIdQuery;
import com.metasoft.restyle.platform.reviews.domain.services.ReviewQueryService;
import com.metasoft.restyle.platform.reviews.infrastructure.persistence.jpa.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewQueryServiceImpl implements ReviewQueryService {

    private final ReviewRepository reviewRepository;

    public ReviewQueryServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Optional<Review> handle(GetReviewByIdQuery query) {
        return reviewRepository.findById(query.reviewId());
    }

    @Override
    public Optional<Review> handle(GetReviewByContractorIdAndProjectId query) {
        return reviewRepository.findByContractorIdAndProjectId(query.contractorId(), query.projectId());
    }

    @Override
    public List<Review> handle(GetAllReviewsQuery query) {
        return reviewRepository.findAll();
    }
}
