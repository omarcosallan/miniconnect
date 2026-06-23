package dev.marcos.miniconnect.controller;

import dev.marcos.miniconnect.dto.PrivacyStatusResponseDTO;
import dev.marcos.miniconnect.dto.ProfileDTO;
import dev.marcos.miniconnect.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileDTO> profile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.profile(userId));
    }

    @PostMapping("/{targetUserId}/follow")
    public ResponseEntity<Void> follow(@PathVariable UUID targetUserId) {
        userService.follow(targetUserId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/privacy")
    public ResponseEntity<PrivacyStatusResponseDTO> privacy() {
        return ResponseEntity.ok(userService.togglePrivacy());
    }
}
