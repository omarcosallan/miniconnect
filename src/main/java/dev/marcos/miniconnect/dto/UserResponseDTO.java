package dev.marcos.miniconnect.dto;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        String bio,
        LocalDate birthDate
) {
}
