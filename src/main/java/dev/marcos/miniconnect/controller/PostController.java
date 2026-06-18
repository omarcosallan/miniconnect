package dev.marcos.miniconnect.controller;

import dev.marcos.miniconnect.dto.PostRequestDTO;
import dev.marcos.miniconnect.dto.PostResponseDTO;
import dev.marcos.miniconnect.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostResponseDTO>> feed(Pageable pageable) {
        return ResponseEntity.ok(postService.feed(pageable));
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@Valid @RequestBody PostRequestDTO request) {
        PostResponseDTO createdPost = postService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponseDTO> likePost(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.likePost(postId));
    }
}
