package com.foodrecipes.credentials.credentials.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PagedResponse<T> {
    private List<T> items;
    private LocalDateTime nextCursor;

    public PagedResponse(List<T> items, LocalDateTime nextCursor) {
        this.items = items;
        this.nextCursor = nextCursor;
    }

    public List<T> getItems() {
        return items;
    }

    public LocalDateTime getNextCursor() {
        return nextCursor;
    }
}
