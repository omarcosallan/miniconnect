package dev.marcos.miniconnect.dto;

public record ProfileDTO(
        String name,
        String bio,
        Long posts,
        Long followers,
        Long followings
) {
}
