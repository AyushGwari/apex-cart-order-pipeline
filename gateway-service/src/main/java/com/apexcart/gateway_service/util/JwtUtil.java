package com.apexcart.gateway_service.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil
{
    private static final String SECRET = "U+XCSGRso8Nli+0XzGaH1Ntw5Ss06SnMloepPAd56PjRX7DLbTmPoSEwgvJCT1SG";

    public void validateToken(final String token){
        Jwts.parser().setSigningKey(getSignKey()).build().parseClaimsJws(token);
    }
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey()) // Verify the signature first!
                .build()
                .parseSignedClaims(token)
                .getPayload(); // In older versions of JJWT, this was .getBody()
    }
//    public String extractUsername(String token) {
//        return Jwts.parser()
//                .setSigningKey(getSignKey())
//                .build()
//                .parseClaimsJws(token)
//                .getBody()
//                .getSubject();
//    }
    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
