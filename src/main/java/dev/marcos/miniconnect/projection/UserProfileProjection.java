package dev.marcos.miniconnect.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserProfileProjection {

    UUID getId();
    String getName();
    String getBio();
    LocalDateTime getCreatedAt();
    Long getFollowersCount();
    Long getFollowingCount();
    Long getPostsCount();
}
