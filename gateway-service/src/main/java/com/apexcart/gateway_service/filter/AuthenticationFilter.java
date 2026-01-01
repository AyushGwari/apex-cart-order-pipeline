package com.apexcart.gateway_service.filter;

import com.apexcart.gateway_service.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter(){
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return onError(exchange, "Missing or Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
            }
            String token = authHeader.substring(7);
            try {
                // 3. Validate via JwtUtils
                jwtUtil.validateToken(token);
                Claims claims = jwtUtil.getAllClaimsFromToken(authHeader);
                List<String> roles = claims.get("roles",List.class);
                String rolesHeader = (roles != null) ? String.join(",", roles) : "";
                //   String username= jwtUtil.extractUsername(token);
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("loggedInUser",claims.getSubject()).build();
                return chain.filter(exchange.mutate().request(mutatedRequest).build());
            } catch (Exception e) {
                return onError(exchange, "Unauthorized access: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        });
    }
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }
    public static class Config{}
}
