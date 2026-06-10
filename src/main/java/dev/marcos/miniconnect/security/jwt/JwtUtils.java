package dev.marcos.miniconnect.security.jwt;

import dev.marcos.miniconnect.security.service.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.miniconnect.app.jwtSecret}")
    private String jwtSecret;

    @Value("${spring.miniconnect.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${spring.miniconnect.app.jwtRefreshCookieName}")
    private String jwtRefreshCookieName;

    @Value("${spring.miniconnect.app.jwtCookieName}")
    private String jwtCookieName;

    public String getJwtFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public ResponseCookie generateJwtCookie(UserDetailsImpl user) {
        String jwt = generateToken(user);
        return responseCookie(jwtCookieName, jwt, "/api", jwtExpirationMs / 1000);
    }

    public ResponseCookie getCleanJwtCookie() {
        return responseCookie(jwtCookieName, "", "/api", 0);
    }

    public ResponseCookie generateRefreshJwtCookie(String refreshToken) {
        return responseCookie(jwtRefreshCookieName, refreshToken, "/api/auth/refreshtoken", 24 * 60 * 60 * 7);
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return responseCookie(jwtRefreshCookieName, "", "/api/auth/refreshtoken", 0);
    }

    public String getJwtRefreshFromCookies(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, jwtRefreshCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }

    public String getUserIdFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private String generateToken(UserDetailsImpl user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("name", user.getName())
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    private ResponseCookie responseCookie(String cookieName,
                                          String cookieValue,
                                          String path,
                                          long maxAge) {
        return ResponseCookie.from(cookieName, cookieValue)
                .path(path)
                .maxAge(maxAge)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(true)
                .build();
    }
}
