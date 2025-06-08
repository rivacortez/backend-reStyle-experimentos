package com.metasoft.restyle.platform.reviews.interfaces.rest;

import com.metasoft.restyle.platform.reviews.domain.model.commands.DeleteReviewCommand;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetAllReviewsQuery;
import com.metasoft.restyle.platform.reviews.domain.model.queries.GetReviewByIdQuery;
import com.metasoft.restyle.platform.reviews.domain.services.ReviewCommandService;
import com.metasoft.restyle.platform.reviews.domain.services.ReviewQueryService;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.CreateReviewResource;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.ReviewResource;
import com.metasoft.restyle.platform.reviews.interfaces.rest.resources.UpdateReviewResource;
import com.metasoft.restyle.platform.reviews.interfaces.rest.transform.CreateReviewCommandFromResourceAssembler;
import com.metasoft.restyle.platform.reviews.interfaces.rest.transform.ReviewResourceFromEntityAssembler;
import com.metasoft.restyle.platform.reviews.interfaces.rest.transform.UpdateReviewCommandFomResourceAssembles;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/reviews", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Reviews", description = "Review Management Endpoints")
public class ReviewsController {

    private final ReviewCommandService reviewCommandService;
    private final ReviewQueryService reviewQueryService;

    public ReviewsController(ReviewCommandService reviewCommandService, ReviewQueryService reviewQueryService) {
        this.reviewCommandService = reviewCommandService;
        this.reviewQueryService = reviewQueryService;
    }

    //Post review including contractorId, projectId, duration, rating, comment, image
    @PostMapping
    public ResponseEntity<ReviewResource> createReview(@RequestBody CreateReviewResource resource) {
        var createReviewCommand = CreateReviewCommandFromResourceAssembler.toCommandResource(resource);
        var reviewId = reviewCommandService.handle(createReviewCommand);
        if (reviewId == 0L){
            return ResponseEntity.badRequest().build();
        }
        var getReviewByIdQuery = new GetReviewByIdQuery(reviewId);
        var review = reviewQueryService.handle(getReviewByIdQuery);

        if (review.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        var reviewResource = ReviewResourceFromEntityAssembler.toResourceFromEntity(review.get());
        return new ResponseEntity<>(reviewResource,HttpStatus.CREATED);
    }

    // Get all reviews
    @GetMapping
    public ResponseEntity<List<ReviewResource>> getAllReviews() {
        var getAllReviewsQuery = new GetAllReviewsQuery();
        var reviews = reviewQueryService.handle(getAllReviewsQuery);
        var reviewResources = reviews.stream().map(ReviewResourceFromEntityAssembler::toResourceFromEntity).toList();
        return ResponseEntity.ok(reviewResources);
    }

    // Delete review by id
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        try {
            var deleteReviewCommand = new DeleteReviewCommand(reviewId);
            reviewCommandService.handle(deleteReviewCommand);
            return ResponseEntity.ok("Review deleted successfully");
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }

    // Update review by id
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResource> updateReview(@PathVariable Long reviewId, @RequestBody UpdateReviewResource resource) {
        var updateReviewCommand = UpdateReviewCommandFomResourceAssembles.toCommandFromResource(reviewId, resource);
        reviewCommandService.handle(updateReviewCommand);

        var getReviewByIdQuery = new GetReviewByIdQuery(reviewId);
        var review = reviewQueryService.handle(getReviewByIdQuery);

        if (review.isEmpty()){
            return ResponseEntity.notFound().build();
        }
        var reviewResource = ReviewResourceFromEntityAssembler.toResourceFromEntity(review.get());
        return ResponseEntity.ok(reviewResource);
    }

    // Get review by contractorId and projectId

}
