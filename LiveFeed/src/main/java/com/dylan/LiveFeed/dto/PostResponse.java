package com.dylan.LiveFeed.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long id,
        String message,
        String username,
        LocalDateTime createdAt,
        boolean updated,
        LocalDateTime updatedAt,
        int likeCount
) {
}
