package com.github.cyberxandrew.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    // Извлекает имя пользователя из JWT токена
    public String extractLoginFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // Извлекает дату истечения срока действия из JWT токена
    public Date extractExpirationFromToken(String  token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey()) //Устанавливает ключ подписи (signing key), который будет использоваться для проверки подписи JWT токена.
                .build()
                .parseClaimsJws(token) //Разбирает JWT токен и проверяет его подпись. Если подпись недействительна, этот метод выбросит исключение.
                .getBody(); //Возвращает объект Claims, содержащий все утверждения из JWT токена.
    }

    private Key getSignInKey() { // Key представляет собой криптографический ключ, используемый для подписи и проверки JWT токенов
        byte[] keyBytes = Decoders.BASE64.decode(secret); // преобразует наш секретный ключ (который хранится в виде строки в поле secret) в массив байтов.
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes); //создает объект Key из массива байтов. Используется Keys.hmacShaKeyFor(), чтобы создать ключ для алгоритма HMAC-SHA который мы используем для подписи JWT токенов
        return secretKey;
    }

    private boolean isTokenExpired(String token) {
        Date date = extractExpirationFromToken(token); //получает дату истечения срока действия токена
        boolean expired = date.before(new Date()); //Сравнивает дату истечения срока действия токена с текущей датой и временем
        return expired;
    }
    // Генерирует токен JWT
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder() //Создает объект JwtBuilder, который используется для создания JWT токенов
                .setClaims(extraClaims) //Устанавливает дополнительные утверждения (claims) для JWT токена
                .setSubject(userDetails.getUsername()) //Устанавливает “subject” (тема) JWT токена. В данном случае мы устанавливаем имя пользователя в качестве темы
                .setIssuedAt(new Date(System.currentTimeMillis())) //Устанавливает время выдачи (issued at) JWT токена.
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) //Устанавливает дату истечения срока действия (expiration date) JWT токена
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) //Подписывает JWT токен с помощью секретного ключа и алгоритма HMAC-SHA256
                .compact();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }
    //Валидирует токен, проверяя имя пользователя и срок действия.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        String login = extractLoginFromToken(token);  // извлекаем имя пользователя из JWT токена
        return login.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}
