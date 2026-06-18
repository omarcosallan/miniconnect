package dev.marcos.miniconnect.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponseDTO(
        UUID id,
        String content,
        String authorName,
        Long likesCount,
        Long commentsCount,
        LocalDateTime createdAt
) {
}
