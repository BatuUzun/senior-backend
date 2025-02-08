package com.foodrecipes.profileapi.dto;

public class FollowCountsDTO {
    private long followingsCount;
    private long followersCount;
    private long recipeCount;

    public FollowCountsDTO(long followingsCount, long followersCount, long recipeCount) {
        this.followingsCount = followingsCount;
        this.followersCount = followersCount;
        this.recipeCount = recipeCount;
    }

    public long getFollowingsCount() {
        return followingsCount;
    }

    public void setFollowingsCount(long followingsCount) {
        this.followingsCount = followingsCount;
    }

    public long getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(long followersCount) {
        this.followersCount = followersCount;
    }

	public FollowCountsDTO() {
		
	}

	public long getRecipeCount() {
		return recipeCount;
	}

	public void setRecipeCount(long recipeCount) {
		this.recipeCount = recipeCount;
	}
    
    
}
