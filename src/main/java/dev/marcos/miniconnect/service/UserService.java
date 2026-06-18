package dev.marcos.miniconnect.service;

import dev.marcos.miniconnect.dto.ProfileDTO;
import dev.marcos.miniconnect.exception.ResourceNotFoundException;
import dev.marcos.miniconnect.model.User;
import dev.marcos.miniconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
