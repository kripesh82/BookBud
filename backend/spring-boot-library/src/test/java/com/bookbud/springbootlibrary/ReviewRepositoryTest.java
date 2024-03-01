package com.bookbud.springbootlibrary;

import com.bookbud.springbootlibrary.entity.Review;
import com.bookbud.springbootlibrary.repository.ReviewRepository;
import com.bookbud.springbootlibrary.requestmodels.ReviewRequest;
import com.bookbud.springbootlibrary.service.ReviewService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewRepositoryTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    @Order(1)
    @Rollback(value = false)
    public void postReview_Success() throws Exception {
        // Prepare test data
        String userEmail = "test@example.com";
        ReviewRequest reviewRequest = new ReviewRequest();
        reviewRequest.setBookId(1L);
        reviewRequest.setRating(4);
        reviewRequest.setReviewDescription(Optional.of("Test review"));

        // Mocking repository method
        when(reviewRepository.findByUserEmailAndBookId(userEmail, reviewRequest.getBookId())).thenReturn(null);
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review());

        // Execute the method to be tested
        reviewService.postReview(userEmail, reviewRequest);

        // Verify that the review is posted successfully
        verify(reviewRepository, times(1)).findByUserEmailAndBookId(userEmail, reviewRequest.getBookId());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    public void userReviewListed_True() {
        // Prepare test data
        String userEmail = "test@example.com";
        long bookId = 1L;
        Review existingReview = new Review();
        existingReview.setId(1L);

        // Mocking repository method
        when(reviewRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(existingReview);

        // Execute the method to be tested
        boolean isReviewListed = reviewService.userReviewListed(userEmail, bookId);

        // Verify that the user review is listed
        assertTrue(isReviewListed);
        verify(reviewRepository, times(1)).findByUserEmailAndBookId(userEmail, bookId);
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    public void userReviewListed_False() {
        // Prepare test data
        String userEmail = "test@example.com";
        long bookId = 1L;

        // Mocking repository method
        when(reviewRepository.findByUserEmailAndBookId(userEmail, bookId)).thenReturn(null);

        // Execute the method to be tested
        boolean isReviewListed = reviewService.userReviewListed(userEmail, bookId);

        // Verify that the user review is not listed
        assertFalse(isReviewListed);
        verify(reviewRepository, times(1)).findByUserEmailAndBookId(userEmail, bookId);
    }
}
