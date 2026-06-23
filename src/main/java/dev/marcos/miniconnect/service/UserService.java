package dev.marcos.miniconnect.service;

import dev.marcos.miniconnect.dto.PrivacyStatusResponseDTO;
import dev.marcos.miniconnect.dto.ProfileDTO;
import dev.marcos.miniconnect.exception.ResourceNotFoundException;
import dev.marcos.miniconnect.model.User;
import dev.marcos.miniconnect.projection.UserProfileProjection;
import dev.marcos.miniconnect.repository.UserRepository;
import dev.marcos.miniconnect.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ProfileDTO profile(UUID userId) {
        UserProfileProjection user = userRepository.findUserProfile(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return new ProfileDTO(
                user.getId(),
                user.getName(),
                user.getBio(),
                user.getIsPrivate(),
                user.getCreatedAt(),
                user.getFollowersCount(),
                user.getFollowingCount(),
                user.getPostsCount()
        );
    }

    @Transactional
    public void follow(UUID targetUserId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        if (userDetails.getId().equals(targetUserId)) {
            throw new IllegalArgumentException("Você não pode seguir a si mesmo.");
        }

        User follower = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado."));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Perfil alvo não encontrado."));

        follower.toggleFollowing(targetUser);

        userRepository.save(targetUser);
    }

    @Transactional
    public PrivacyStatusResponseDTO togglePrivacy() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário logado não encontrado."));

        user.setPrivate(!user.isPrivate());

        userRepository.save(user);

        String message = user.isPrivate() ? "ACCOUNT_PRIVACY_PRIVATE" : "ACCOUNT_PRIVACY_PUBLIC";

        return new PrivacyStatusResponseDTO(user.isPrivate(), message);
    }
}
