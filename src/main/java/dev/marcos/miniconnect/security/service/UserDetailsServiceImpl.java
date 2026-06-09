package dev.marcos.miniconnect.security.service;

import dev.marcos.miniconnect.model.User;
import dev.marcos.miniconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        try {
            UUID id = UUID.fromString(identifier);
            user = userRepository.findById(id)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com ID: " + identifier));
        } catch (IllegalArgumentException e) {
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com Email: " + identifier));
        }

        return UserDetailsImpl.build(user);
    }
}
