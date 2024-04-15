package com.app.service.auth;

import com.app.model.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    /**
     * 40 hours.
     */
    private static final long EXPIRATION_DELAY = 100000 * 60 * 24;

    /**
     * Signing key.
     */
    @Value("${token.signing.key}")
    private String jwtSigningKey;

    /**
     * Извлечение имени пользователя из токена.
     *
     * @param token токен
     * @return имя пользователя
     */
    public String extractUserName(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерация токена.
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    public String generateToken(final UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Проверка токена на валидность.
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    public boolean isTokenValid(
            final String token,
            final UserDetails userDetails
    ) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()))
                && !isTokenExpired(token);
    }

    /**
     * Извлечение данных из токена.
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип данных
     * @return данные
     */
    private <T> T extractClaim(
            final String token,
            final Function<Claims, T> claimsResolvers
    ) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация токена.
     *
     * @param extraClaims дополнительные данные
     * @param userDetails данные пользователя
     * @return токен
     */
    private String generateToken(
            final Map<String, Object> extraClaims,
            final UserDetails userDetails
    ) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()
                        + EXPIRATION_DELAY)
                )
                .signWith(getSigningKey()).compact();
    }

    /**
     * Проверка токена на просроченность.
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения токена.
     *
     * @param token токен
     * @return дата истечения
     */
    private Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлечение всех данных из токена.
     *
     * @param token токен
     * @return данные
     */
    private Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Получение ключа для подписи токена.
     *
     * @return ключ
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
