package dev.marcos.miniconnect.service;

import dev.marcos.miniconnect.dto.LoginRequest;
import dev.marcos.miniconnect.dto.RegisterRequest;
import dev.marcos.miniconnect.dto.UserResponseDTO;
import dev.marcos.miniconnect.exception.ResourceAlreadyExistsException;
import dev.marcos.miniconnect.model.User;
import dev.marcos.miniconnect.repository.UserRepository;
import dev.marcos.miniconnect.security.jwt.JwtUtils;
import dev.marcos.miniconnect.security.service.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserResponseDTO register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("O email informado já está em uso!");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);

        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getBio(), user.getBirthDate());
    }

    public ResponseCookie login(LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );
        } catch (AuthenticationException exception) {
            throw new BadCredentialsException("Email ou senha inválidos");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        return jwtUtils.generateJwtCookie(userDetails);
    }

    public ResponseCookie signOut() {
        return jwtUtils.getCleanJwtCookie();
    }
}
