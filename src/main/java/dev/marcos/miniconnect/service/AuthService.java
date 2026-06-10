package dev.marcos.miniconnect.service;

import dev.marcos.miniconnect.dto.AuthCookies;
import dev.marcos.miniconnect.dto.LoginRequestDTO;
import dev.marcos.miniconnect.dto.RegisterRequestDTO;
import dev.marcos.miniconnect.dto.UserResponseDTO;
import dev.marcos.miniconnect.exception.ResourceAlreadyExistsException;
import dev.marcos.miniconnect.model.RefreshToken;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserResponseDTO register(RegisterRequestDTO request) {
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

    public AuthCookies login(LoginRequestDTO request) {
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

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        refreshTokenService.deleteByUserId(userDetails.getId());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        return new AuthCookies(jwtCookie, jwtRefreshCookie);
    }

    public AuthCookies signOut() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!principle.toString().equals("anonymousUser")) {
            UUID userId = ((UserDetailsImpl) principle).getId();
            refreshTokenService.deleteByUserId(userId);
        }

        ResponseCookie cleanJwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie cleanRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return new AuthCookies(cleanJwtCookie, cleanRefreshCookie);
    }

    public ResponseCookie refreshToken(String requestRefreshToken) {
        if (requestRefreshToken == null || requestRefreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh Token não fornecido");
        }

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetailsImpl userDetails = UserDetailsImpl.build(user);
                    return jwtUtils.generateJwtCookie(userDetails);
                })
                .orElseThrow(() -> new IllegalArgumentException("Refresh token inválido ou não encontrado no banco de dados!"));
    }
}
