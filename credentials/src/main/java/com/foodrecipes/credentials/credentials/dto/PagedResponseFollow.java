package com.foodrecipes.credentials.credentials.dto;

import java.util.List;

public class PagedResponseFollow<T> {
    private List<T> items;
    private Integer nextPage;

    // constructor
    public PagedResponseFollow(List<T> items, Integer nextPage) {
        this.items = items;
        this.nextPage = nextPage;
    }

	public List<T> getItems() {
		return items;
	}

	public void setItems(List<T> items) {
		this.items = items;
	}

	public Integer getNextPage() {
		return nextPage;
	}

	public void setNextPage(Integer nextPage) {
		this.nextPage = nextPage;
	}

	public PagedResponseFollow() {
		super();
	}

    // getters and setters (or use Lombok @Data)
    
}
