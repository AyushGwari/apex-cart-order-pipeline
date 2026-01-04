package com.apexcart.gateway_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.function.Function;

@Component
public class JwtUtil
{
    private static final String SECRET = "U+XCSGRso8Nli+0XzGaH1Ntw5Ss06SnMloepPAd56PjRX7DLbTmPoSEwgvJCT1SG";

    public void validateToken(String token){

        Jwts.parser().verifyWith(getSignKey()).build().parseClaimsJws(token);
    }
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey()) // Verify the signature first!
                .build()
                .parseSignedClaims(token)
                .getPayload(); // In older versions of JJWT, this was .getBody()
    }
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
//    public String extractUsername(String token) {
//        return Jwts.parser()
//                .setSigningKey(getSignKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
