package com.apexcart.identity.service;

import com.apexcart.identity.entity.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.*;

@Service
public class JwtService {
    private static final String secretKey = "U+XCSGRso8Nli+0XzGaH1Ntw5Ss06SnMloepPAd56PjRX7DLbTmPoSEwgvJCT1SG";
    public String generateToken(String username,Set<Role>roles){
        Map<String,Object> claims = new HashMap<>();
        List<String> rolesList = roles.stream()
                .map(Role::getName)
                .toList();
        claims.put("roles", rolesList);
        return createToken(claims,username);
    }

    public String createToken(Map<String,Object>claims, String username){
        return Jwts.builder().setClaims(claims).setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*30))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
