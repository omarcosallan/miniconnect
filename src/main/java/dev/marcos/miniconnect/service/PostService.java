package dev.marcos.miniconnect.service;

import dev.marcos.miniconnect.dto.PostRequestDTO;
import dev.marcos.miniconnect.dto.PostResponseDTO;
import dev.marcos.miniconnect.exception.ResourceNotFoundException;
import dev.marcos.miniconnect.model.Post;
import dev.marcos.miniconnect.model.User;
import dev.marcos.miniconnect.repository.PostRepository;
import dev.marcos.miniconnect.repository.UserRepository;
import dev.marcos.miniconnect.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<PostResponseDTO> feed(Pageable pageable) {
        return postRepository.findAllFeedProjected(pageable)
                .map(p -> new PostResponseDTO(
                        p.getId(),
                        p.getContent(),
                        p.getAuthorName(),
                        p.getLikesCount(),
                        p.getCommentsCount(),
                        p.getCreatedAt()
                ));
    }

    @Transactional
    public PostResponseDTO createPost(PostRequestDTO request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Post post = new Post();
        post.setContent(request.content());
        post.setUser(user);

        post = postRepository.save(post);

        return new PostResponseDTO(
                post.getId(),
                post.getContent(),
                user.getName(),
                0L,
                0L,
                post.getCreatedAt()
        );
    }

    @Transactional
    public PostResponseDTO likePost(UUID postId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post não encontrado."));

        post.toggleLike(user);

        postRepository.save(post);

        return new PostResponseDTO(
                post.getId(),
                post.getContent(),
                post.getUser().getName(),
                (long) post.getLikes().size(),
                (long) post.getComments().size(),
                post.getCreatedAt()
        );
    }
}
