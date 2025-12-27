package com.apexcart.gateway_service.config;

import com.apexcart.gateway_service.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, AuthenticationFilter authFilter) {
        return builder.routes()
                // Route 1: Identity Service (Open)
                .route("identity-service", r -> r.path("/api/v1/auth/**")
                        .uri("lb://IDENTITY-SERVICE"))

                // Route 2: Order Service (Protected by your Filter)
                .route("order-service", r -> r.path("/api/v1/orders/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://ORDER-SERVICE")) //dynamic lookup to url due to eureka service discovery
                .build();
    }

}
