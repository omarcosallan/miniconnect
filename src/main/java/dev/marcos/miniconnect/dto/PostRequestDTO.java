package dev.marcos.miniconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PostRequestDTO(
        @NotBlank(message = "O conteúdo da postagem não pode estar vazio.")
        @Size(max = 280, message = "A postagem deve ter no máximo 280 caracteres.")
        String content
) {
}
