package dev.marcos.miniconnect.dto;

public record PrivacyStatusResponseDTO(
        boolean isPrivate,
        String message
) {
}
