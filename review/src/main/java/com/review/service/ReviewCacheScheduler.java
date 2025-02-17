package com.review.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReviewCacheScheduler {

    private final ReviewService reviewService;

    @Autowired
    public ReviewCacheScheduler(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /** ✅ Refresh the Global Cache Every 1 Hour */
    @Scheduled(cron = "0 0 0 * * ?") // Runs every day at midnight (00:00:00)
    public void updatePopularReviewsCache() {
        reviewService.initializeGlobalPopularReviewsCache();
    }
}
