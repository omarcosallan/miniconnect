package dev.marcos.miniconnect.dto;

import org.springframework.http.ResponseCookie;

public record AuthCookies(ResponseCookie jwtCookie, ResponseCookie refreshCookie) {
}
