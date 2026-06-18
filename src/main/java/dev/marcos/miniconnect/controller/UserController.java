package dev.marcos.miniconnect.controller;

import dev.marcos.miniconnect.dto.ProfileDTO;
import dev.marcos.miniconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/profile")
    public ResponseEntity<ProfileDTO> profile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.profile(userId));
    }
}
