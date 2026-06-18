package dev.marcos.miniconnect.service;

import dev.marcos.miniconnect.dto.ProfileDTO;
import dev.marcos.miniconnect.exception.ResourceNotFoundException;
import dev.marcos.miniconnect.model.User;
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
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        return new ProfileDTO(
                user.getName(),
                user.getBio(),
                (long) user.getPosts().size(),
                (long) user.getFollowers().size(),
                (long) user.getFollowing().size()
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
}
