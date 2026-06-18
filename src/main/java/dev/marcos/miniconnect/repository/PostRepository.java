package dev.marcos.miniconnect.repository;

import dev.marcos.miniconnect.model.Post;
import dev.marcos.miniconnect.projection.PostFeedProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    @Query(value = """
            SELECT
                p.id AS id,
                p.content AS content,
                u.name AS authorName,
                (SELECT COUNT(*) FROM like_posts lp WHERE lp.post_id = p.id) AS likesCount,
                (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) AS commentsCount,
                p.created_at AS createdAt
            FROM posts p
            INNER JOIN users u ON p.user_id = u.id
            """,
            countQuery = "SELECT COUNT(*) FROM posts",
            nativeQuery = true)
    Page<PostFeedProjection> findAllFeedProjected(Pageable pageable);
}
