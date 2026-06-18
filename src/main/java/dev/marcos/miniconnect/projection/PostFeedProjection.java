package dev.marcos.miniconnect.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface PostFeedProjection {
    UUID getId();
    String getContent();
    String getAuthorName();
    Long getLikesCount();
    Long getCommentsCount();
    LocalDateTime getCreatedAt();
}
