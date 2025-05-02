package com.foodrecipes.credentials.credentials.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodrecipes.credentials.credentials.constants.Constants;
import com.foodrecipes.credentials.credentials.dto.LikeActivityDTO;
import com.foodrecipes.credentials.credentials.dto.PagedResponseActivity;
import com.foodrecipes.credentials.credentials.dto.PagedResponseFollow;
import com.foodrecipes.credentials.credentials.dto.PagedReviewResponse;
import com.foodrecipes.credentials.credentials.dto.ReviewCommentActivityDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewDTO;
import com.foodrecipes.credentials.credentials.dto.ReviewLikeActivityDTO;
import com.foodrecipes.credentials.credentials.dto.UnifiedActivityDTO;
import com.foodrecipes.credentials.credentials.dto.UserFollowProjection;
import com.foodrecipes.credentials.credentials.dto.UserProfileResponseProfileGetterDTO;
import com.foodrecipes.credentials.credentials.entity.Like;
import com.foodrecipes.credentials.credentials.entity.Review;
import com.foodrecipes.credentials.credentials.entity.ReviewCommentC;
import com.foodrecipes.credentials.credentials.entity.ReviewLike;
import com.foodrecipes.credentials.credentials.entity.UserFollow;
import com.foodrecipes.credentials.credentials.repository.LikeRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewCommentRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewLikeRepository;
import com.foodrecipes.credentials.credentials.repository.ReviewRepository;
import com.foodrecipes.credentials.credentials.repository.UserFollowsRepository;

@Service
public class UserFollowsService {

	@Autowired
	private UserFollowsRepository userFollowsRepository;
	@Autowired
    private ReviewRepository reviewRepository;
	@Autowired
    private ReviewLikeRepository reviewLikeRepository;
	@Autowired
	private LikeRepository likeRepository;
	@Autowired
	private UserProfileService uProfileService;
	@Autowired
	private ReviewCommentRepository reviewCommentRepository;
	
	
	@Transactional
	public String followUser(Long followerId, Long followedId) {
		if (followerId.equals(followedId)) {
			return "You cannot follow yourself!";
		}

		boolean alreadyFollowing = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
		if (alreadyFollowing) {
			return "You are already following this user.";
		}

		UserFollow userFollow = new UserFollow();
		userFollow.setFollowerId(followerId);
		userFollow.setFollowedId(followedId);
		userFollowsRepository.save(userFollow);

		return "Successfully followed the user.";
	}

	@Transactional
	public String unfollowUser(Long followerId, Long followedId) {
		boolean alreadyFollowing = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
		if (!alreadyFollowing) {
			return "You are not following this user.";
		}

		userFollowsRepository.deleteByFollowerIdAndFollowedId(followerId, followedId);

		return "Successfully unfollowed the user.";
	}

	public boolean isFollowing(Long followerId, Long followedId) {
		// Check Redis first

		// Fallback to MySQL (if Redis doesn't have the data)
		boolean existsInDB = userFollowsRepository.existsByFollowerIdAndFollowedId(followerId, followedId);

		return existsInDB;
	}

	public long getFollowerCount(Long userProfileId) {

		long count = userFollowsRepository.countByFollowedId(userProfileId);

		return count; // Ensure a non-null value is returned
	}

	public long getFollowingCount(Long userProfileId) {

		long count = userFollowsRepository.countByFollowerId(userProfileId);

		return count; // Ensure a non-null value is returned
	}

    @Autowired
    private UserProfileService userProfileService;


    /*public PagedResponse<UserProfileResponseProfileGetterDTO> getFollowings(Long userId, LocalDateTime cursor) {
        if (cursor == null) {
            cursor = LocalDateTime.of(2000, 1, 1, 0, 0);
        }

        List<UserFollowProjection> projections = userFollowsRepository.findFollowingsWithCursor(userId, cursor, PageRequest.of(0, Constants.PAGE_SIZE));
        List<Long> userIds = projections.stream().map(UserFollowProjection::getUserId).collect(Collectors.toList());
        List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(userIds);

        LocalDateTime nextCursor = projections.size() == Constants.PAGE_SIZE
            ? projections.get(projections.size() - 1).getDateFollowed()
            : null;

        return new PagedResponse<>(profiles, nextCursor);
    }*/
    public PagedResponseFollow<UserProfileResponseProfileGetterDTO> getFollowings(Long userId, int page) {
        Page<UserFollowProjection> projectionsPage = userFollowsRepository.findFollowingsByUserId(
            userId,
            PageRequest.of(page, Constants.PAGE_SIZE)
        );

        List<Long> userIds = projectionsPage.stream()
            .map(UserFollowProjection::getUserId)
            .collect(Collectors.toList());

        List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(userIds);

        Integer nextPage = projectionsPage.hasNext() ? page + 1 : null;

        return new PagedResponseFollow<>(profiles, nextPage);
    }




	/*public PagedResponse<UserProfileResponseProfileGetterDTO> getFollowers(Long userId, LocalDateTime cursor) {
	    if (cursor == null) {
	        cursor = LocalDateTime.of(2000, 1, 1, 0, 0);
	    }

	    List<UserFollowProjection> projections = userFollowsRepository.findFollowersWithCursor(userId, cursor, Constants.PAGE_SIZE);
	    List<Long> userIds = projections.stream().map(UserFollowProjection::getUserId).collect(Collectors.toList());
	    List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(userIds);

	    LocalDateTime nextCursor = projections.size() == Constants.PAGE_SIZE
	        ? projections.get(projections.size() - 1).getDateFollowed()
	        : null;

	    return new PagedResponse<>(profiles, nextCursor);
	}*/
    public PagedResponseFollow<UserProfileResponseProfileGetterDTO> getFollowers(Long userId, int page) {
        Pageable pageable = PageRequest.of(page, Constants.PAGE_SIZE);
        Page<UserFollowProjection> projectionPage = userFollowsRepository.findFollowersByUserId(userId, pageable);

        List<Long> userIds = projectionPage.stream().map(UserFollowProjection::getUserId).toList();
        List<UserProfileResponseProfileGetterDTO> profiles = userProfileService.getUserProfilesByIds(userIds);

        Integer nextPage = projectionPage.hasNext() ? page + 1 : null;
        return new PagedResponseFollow<>(profiles, nextPage);
    }



	public Set<Long> getFollowedUsers(Long userId) {
	    return userFollowsRepository.findFollowedUsersByUserId(userId);
	}

	public PagedResponseActivity getInteractionsByUserIds(
	        List<Long> userIds,
	        LocalDateTime reviewCursor,
	        LocalDateTime reviewLikeCursor,
	        LocalDateTime likeCursor,
	        LocalDateTime commentCursor
	) {
	    boolean allNull = reviewCursor == null && reviewLikeCursor == null && likeCursor == null && commentCursor == null;

	    if (allNull) {
	        reviewCursor = LocalDateTime.now();
	        reviewLikeCursor = LocalDateTime.now();
	        likeCursor = LocalDateTime.now();
	        commentCursor = LocalDateTime.now();
	        System.out.println("1");

	    } else {
	        LocalDateTime oldDate = LocalDateTime.of(1900, 1, 1, 0, 0);
	        if (reviewCursor == null) reviewCursor = oldDate;
	        if (reviewLikeCursor == null) reviewLikeCursor = oldDate;
	        if (likeCursor == null) likeCursor = oldDate;
	        if (commentCursor == null) commentCursor = oldDate;
	        System.out.println("2");
	    }

	    // === Fetch Reviews ===
	    List<Review> reviewEntities = reviewRepository.findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userIds, reviewCursor);
	    List<ReviewDTO> reviews = reviewEntities.stream().limit(10)
	        .map(r -> new ReviewDTO(r.getId(), r.getUserId(), r.getSpotifyId(), r.getRating(), r.getComment(), r.getCreatedAt()))
	        .toList();
	    LocalDateTime reviewNextCursor = reviewEntities.size() > 10
	        ? reviewEntities.get(10).getCreatedAt()
	        : null;

	    // === Fetch Review Likes ===
	    List<ReviewLike> reviewLikeEntities = reviewLikeRepository.findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userIds, reviewLikeCursor);
	    List<ReviewLikeActivityDTO> reviewLikes = reviewLikeEntities.stream().limit(10)
	        .map(rl -> new ReviewLikeActivityDTO(rl.getId(), rl.getUserId(), rl.getReview().getId(), rl.getCreatedAt()))
	        .toList();
	    LocalDateTime reviewLikeNextCursor = reviewLikeEntities.size() > 10
	        ? reviewLikeEntities.get(10).getCreatedAt()
	        : null;
	    
	    
	 // === Fetch Review Comments ===
	    List<ReviewCommentC> reviewCommentEntities = reviewCommentRepository.findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userIds, reviewLikeCursor);
	    List<ReviewCommentActivityDTO> reviewComments = reviewCommentEntities.stream().limit(10)
	    		.map(rl -> new ReviewCommentActivityDTO(rl.getId(), rl.getUserId(), rl.getReview().getId(), rl.getCreatedAt(), rl.getComment()))
	        .toList();
	    LocalDateTime reviewCommentNextCursor = reviewCommentEntities.size() > 10
	        ? reviewCommentEntities.get(10).getCreatedAt()
	        : null;

	    // === Fetch Likes ===
	    List<Like> likeEntities = likeRepository.findTop11ByUserIdInAndCreatedAtBeforeOrderByCreatedAtDesc(userIds, likeCursor);
	    List<LikeActivityDTO> likes = likeEntities.stream().limit(10)
	        .map(l -> new LikeActivityDTO(l.getId(), l.getUserId(), l.getSpotifyId(), l.getType(), l.getCreatedAt()))
	        .toList();
	    LocalDateTime likeNextCursor = likeEntities.size() > 10
	        ? likeEntities.get(10).getCreatedAt()
	        : null;

	    // === Collect all involved userIds from activity (may include duplicates)
	    Set<Long> involvedUserIds = new HashSet<>();
	    reviews.forEach(r -> involvedUserIds.add(r.getUserId()));
	    reviewLikes.forEach(rl -> {
	        involvedUserIds.add(rl.getUserId());
	        reviewRepository.findById(rl.getReviewId()).ifPresent(r -> involvedUserIds.add(r.getUserId()));
	    });
	    reviewComments.forEach(rl -> {
	        involvedUserIds.add(rl.getUserId());
	        reviewRepository.findById(rl.getReviewId()).ifPresent(r -> involvedUserIds.add(r.getUserId()));
	    });
	    likes.forEach(l -> involvedUserIds.add(l.getUserId()));

	    // === Fetch user profile details
	    List<UserProfileResponseProfileGetterDTO> profiles = uProfileService.getUserProfilesByIdsNoLimitation(new ArrayList<>(involvedUserIds));
	    Map<Long, UserProfileResponseProfileGetterDTO> profileMap = profiles.stream()
	        .collect(Collectors.toMap(UserProfileResponseProfileGetterDTO::getUserId, p -> p));

	    // === Build Unified Activity List with profile info
	    List<UnifiedActivityDTO> activities = new ArrayList<>();

	    reviews.forEach(r -> {
	        UserProfileResponseProfileGetterDTO profile = profileMap.get(r.getUserId());
	        activities.add(new UnifiedActivityDTO("review", r.getId(), r.getUserId(), r.getCreatedAt(), r, profile));
	    });

	    reviewLikes.forEach(rl -> {
	        UserProfileResponseProfileGetterDTO likerProfile = profileMap.get(rl.getUserId());

	        Optional<Review> likedReviewOpt = Optional.empty();
	        try {
	            likedReviewOpt = reviewRepository.findById(rl.getReviewId());
	        } catch (Exception e) {
	            System.out.println("⚠️ Review not found for reviewLike ID " + rl.getId());
	        }

	        Map<String, Object> combinedDetails = new HashMap<>();
	        combinedDetails.put("reviewLike", rl);

	        if (likedReviewOpt.isPresent()) {
	            Review r = likedReviewOpt.get();
	            ReviewDTO reviewDto = new ReviewDTO(r.getId(), r.getUserId(), r.getSpotifyId(), r.getRating(), r.getComment(), r.getCreatedAt());
	            combinedDetails.put("review", reviewDto);

	            // 💡 Fetch review author's profile and attach
	            UserProfileResponseProfileGetterDTO authorProfile = profileMap.get(r.getUserId());
	            combinedDetails.put("reviewAuthorProfile", authorProfile);
	        }

	        activities.add(new UnifiedActivityDTO("reviewLike", rl.getId(), rl.getUserId(), rl.getCreatedAt(), combinedDetails, likerProfile));
	    });
	    
	    
	    
	    reviewComments.forEach(rl -> {
	        UserProfileResponseProfileGetterDTO commentProfile = profileMap.get(rl.getUserId());

	        Optional<Review> commentReviewOpt = Optional.empty();
	        try {
	        	commentReviewOpt = reviewRepository.findById(rl.getReviewId());
	        } catch (Exception e) {
	            System.out.println("⚠️ Review not found for reviewLike ID " + rl.getId());
	        }

	        Map<String, Object> combinedDetails = new HashMap<>();
	        combinedDetails.put("reviewComment", rl);

	        if (commentReviewOpt.isPresent()) {
	            Review r = commentReviewOpt.get();
	            ReviewDTO reviewDto = new ReviewDTO(r.getId(), r.getUserId(), r.getSpotifyId(), r.getRating(), r.getComment(), r.getCreatedAt());
	            combinedDetails.put("review", reviewDto);

	            // 💡 Fetch review author's profile and attach
	            UserProfileResponseProfileGetterDTO authorProfile = profileMap.get(r.getUserId());
	            combinedDetails.put("reviewAuthorProfile", authorProfile);
	        }

	        activities.add(new UnifiedActivityDTO("reviewComment", rl.getId(), rl.getUserId(), rl.getCreatedAt(), combinedDetails, commentProfile));
	    });




	    likes.forEach(l -> {
	        UserProfileResponseProfileGetterDTO profile = profileMap.get(l.getUserId());
	        activities.add(new UnifiedActivityDTO("like", l.getId(), l.getUserId(), l.getCreatedAt(), l, profile));
	    });

	    // === Sort all by createdAt DESC
	    activities.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));

	    // === Final grouped data map
	    Map<String, Object> raw = new HashMap<>();
	    raw.put("reviews", reviews);
	    raw.put("reviewLikes", reviewLikes);
	    raw.put("likes", likes);
	    raw.put("reviewComments", reviewComments);

	    return new PagedResponseActivity(activities, raw, reviewNextCursor, reviewLikeNextCursor, likeNextCursor, reviewCommentNextCursor);
	}



	public PagedReviewResponse getReviewActivities(List<Long> userIds, LocalDateTime reviewCursor) {
	    if (reviewCursor == null) {
	        reviewCursor = LocalDateTime.now();
	    }

	    List<Review> reviewEntities = reviewRepository.findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userIds, reviewCursor);
	    List<ReviewDTO> reviews = reviewEntities.stream().limit(10)
	        .map(r -> new ReviewDTO(r.getId(), r.getUserId(), r.getSpotifyId(), r.getRating(), r.getComment(), r.getCreatedAt()))
	        .toList();
	    LocalDateTime reviewNextCursor = reviewEntities.size() > 10 ? reviewEntities.get(10).getCreatedAt() : null;

	    List<UserProfileResponseProfileGetterDTO> profiles = uProfileService.getUserProfilesByIdsNoLimitation(
	        reviews.stream().map(ReviewDTO::getUserId).distinct().toList()
	    );
	    Map<Long, UserProfileResponseProfileGetterDTO> profileMap = profiles.stream()
	        .collect(Collectors.toMap(UserProfileResponseProfileGetterDTO::getUserId, p -> p));

	    List<UnifiedActivityDTO> activities = reviews.stream()
	        .map(r -> new UnifiedActivityDTO("review", r.getId(), r.getUserId(), r.getCreatedAt(), r, profileMap.get(r.getUserId())))
	        .toList();

	    return new PagedReviewResponse(activities, reviewNextCursor);
	}

	public PagedReviewResponse getReviewLikeActivities(List<Long> userIds, LocalDateTime reviewLikeCursor) {
	    if (reviewLikeCursor == null) {
	        reviewLikeCursor = LocalDateTime.now();
	    }

	    List<ReviewLike> reviewLikeEntities = reviewLikeRepository.findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userIds, reviewLikeCursor);
	    List<ReviewLikeActivityDTO> reviewLikes = reviewLikeEntities.stream().limit(10)
	        .map(rl -> new ReviewLikeActivityDTO(rl.getId(), rl.getUserId(), rl.getReview().getId(), rl.getCreatedAt()))
	        .toList();
	    LocalDateTime reviewLikeNextCursor = reviewLikeEntities.size() > 10 ? reviewLikeEntities.get(10).getCreatedAt() : null;

	    // ✅ Fetch all Review entities for the liked reviews
	    List<Review> reviewList = reviewRepository.findAllById(
	        reviewLikes.stream().map(ReviewLikeActivityDTO::getReviewId).distinct().toList()
	    );
	    Map<Long, Review> reviewMap = reviewList.stream().collect(Collectors.toMap(Review::getId, r -> r));

	    // ✅ Correct involved user IDs: liker users + review owners
	    Set<Long> involvedUserIds = new HashSet<>();
	    involvedUserIds.addAll(reviewLikes.stream().map(ReviewLikeActivityDTO::getUserId).toList()); // liker users
	    involvedUserIds.addAll(reviewList.stream().map(Review::getUserId).toList()); // review owners

	    // ✅ Fetch profiles for all involved users
	    List<UserProfileResponseProfileGetterDTO> profiles = uProfileService.getUserProfilesByIdsNoLimitation(new ArrayList<>(involvedUserIds));
	    Map<Long, UserProfileResponseProfileGetterDTO> profileMap = profiles.stream()
	        .collect(Collectors.toMap(UserProfileResponseProfileGetterDTO::getUserId, p -> p));

	    List<UnifiedActivityDTO> activities = new ArrayList<>();
	    for (ReviewLikeActivityDTO rl : reviewLikes) {
	        Review r = reviewMap.get(rl.getReviewId());
	        if (r == null) continue;
	        Map<String, Object> details = new HashMap<>();
	        details.put("reviewLike", rl);
	        details.put("review", new ReviewDTO(r.getId(), r.getUserId(), r.getSpotifyId(), r.getRating(), r.getComment(), r.getCreatedAt()));
	        details.put("reviewAuthorProfile", profileMap.get(r.getUserId())); // ✅ will not be null now!

	        activities.add(new UnifiedActivityDTO(
	            "reviewLike",
	            rl.getId(),
	            rl.getUserId(),
	            rl.getCreatedAt(),
	            details,
	            profileMap.get(rl.getUserId()) // liker profile
	        ));
	    }

	    return new PagedReviewResponse(activities, reviewLikeNextCursor);
	}


	public PagedReviewResponse getReviewCommentActivities(List<Long> userIds, LocalDateTime commentCursor) {
	    if (commentCursor == null) {
	        commentCursor = LocalDateTime.now();
	    }

	    List<ReviewCommentC> commentEntities = reviewCommentRepository.findTop11ByUserIdInAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userIds, commentCursor);
	    List<ReviewCommentActivityDTO> commentDTOs = commentEntities.stream().limit(10)
	        .map(rc -> new ReviewCommentActivityDTO(rc.getId(), rc.getUserId(), rc.getReview().getId(), rc.getCreatedAt(), rc.getComment()))
	        .toList();
	    LocalDateTime commentNextCursor = commentEntities.size() > 10 ? commentEntities.get(10).getCreatedAt() : null;

	    // ✅ Fetch all Review entities based on comment.reviewId
	    List<Review> reviewList = reviewRepository.findAllById(
	        commentDTOs.stream().map(ReviewCommentActivityDTO::getReviewId).distinct().toList()
	    );
	    Map<Long, Review> reviewMap = reviewList.stream().collect(Collectors.toMap(Review::getId, r -> r));

	    // ✅ Now correctly collect user IDs
	    Set<Long> involvedUserIds = new HashSet<>();
	    involvedUserIds.addAll(commentDTOs.stream().map(ReviewCommentActivityDTO::getUserId).toList()); // comment owner (commenter)
	    involvedUserIds.addAll(reviewList.stream().map(Review::getUserId).toList()); // review owner

	    // ✅ Fetch their profiles
	    List<UserProfileResponseProfileGetterDTO> profiles = uProfileService.getUserProfilesByIdsNoLimitation(new ArrayList<>(involvedUserIds));
	    Map<Long, UserProfileResponseProfileGetterDTO> profileMap = profiles.stream()
	        .collect(Collectors.toMap(UserProfileResponseProfileGetterDTO::getUserId, p -> p));

	    List<UnifiedActivityDTO> activities = new ArrayList<>();
	    for (ReviewCommentActivityDTO rc : commentDTOs) {
	        Review r = reviewMap.get(rc.getReviewId());
	        if (r == null) continue;
	        Map<String, Object> details = new HashMap<>();
	        details.put("reviewComment", rc);
	        details.put("review", new ReviewDTO(r.getId(), r.getUserId(), r.getSpotifyId(), r.getRating(), r.getComment(), r.getCreatedAt()));
	        details.put("reviewAuthorProfile", profileMap.get(r.getUserId())); // ✅ Now will not be null

	        activities.add(new UnifiedActivityDTO(
	            "reviewComment",
	            rc.getId(),
	            rc.getUserId(),
	            rc.getCreatedAt(),
	            details,
	            profileMap.get(rc.getUserId()) // commenter profile
	        ));
	    }

	    return new PagedReviewResponse(activities, commentNextCursor);
	}


}
