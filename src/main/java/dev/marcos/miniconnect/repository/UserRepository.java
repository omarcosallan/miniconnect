package dev.marcos.miniconnect.repository;

import dev.marcos.miniconnect.model.User;
import dev.marcos.miniconnect.projection.UserProfileProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query(value = """
            SELECT 
                u.id AS id,
                u.name AS name,
                u.bio AS bio,
                u.created_at AS createdAt,
                (SELECT COUNT(*) FROM followers f WHERE f.followed_id = u.id) AS followersCount,
                (SELECT COUNT(*) FROM followers f WHERE f.follower_id = u.id) AS followingCount,
                (SELECT COUNT(*) FROM posts p WHERE p.user_id = u.id) AS postsCount
            FROM users u
            WHERE u.id = :userId
            """, nativeQuery = true)
    Optional<UserProfileProjection> findUserProfile(@Param("userId") UUID userId);
}
