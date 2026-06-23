package dev.marcos.miniconnect.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfileDTO(
        UUID id,
        String name,
        String bio,
        boolean isPrivate,
        LocalDateTime createdAt,
        Long followersCount,
        Long followingCount,
        Long postCount
) {
}
