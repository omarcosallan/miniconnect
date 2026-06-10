package dev.marcos.miniconnect.controller;

import dev.marcos.miniconnect.dto.AuthCookies;
import dev.marcos.miniconnect.dto.LoginRequestDTO;
import dev.marcos.miniconnect.dto.RegisterRequestDTO;
import dev.marcos.miniconnect.dto.UserResponseDTO;
import dev.marcos.miniconnect.security.jwt.JwtUtils;
import dev.marcos.miniconnect.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO signUpRequest) {
        UserResponseDTO userResponseDTO = authService.register(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthCookies cookies = authService.login(loginRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookies.jwtCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookies.refreshCookie().toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        AuthCookies cleanCookies = authService.signOut();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cleanCookies.jwtCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cleanCookies.refreshCookie().toString())
                .build();
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        ResponseCookie newJwtCookie = authService.refreshToken(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newJwtCookie.toString())
                .build();
    }
}
