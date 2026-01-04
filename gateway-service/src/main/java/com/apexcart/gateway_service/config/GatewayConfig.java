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
                // --- IDENTITY SERVICE ---
                .route("identity-service", r -> r.path("/api/v1/auth/**")
                        .uri("lb://IDENTITY-SERVICE"))

                // Route for Identity Docs (MUST be before or separate from the main route)
                .route("identity-docs", r -> r.path("/IDENTITY-SERVICE/v3/api-docs/**")
                        .filters(f -> f.rewritePath("/IDENTITY-SERVICE/(?<path>.*)", "/${path}"))
                        .uri("lb://IDENTITY-SERVICE"))

                // --- ORDER SERVICE ---
                .route("order-service", r -> r.path("/api/v1/orders/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://ORDER-SERVICE"))

                .route("order-docs", r -> r.path("/ORDER-SERVICE/v3/api-docs/**")
                        .filters(f -> f.rewritePath("/ORDER-SERVICE/(?<path>.*)", "/${path}"))
                        .uri("lb://ORDER-SERVICE"))

                // --- INVENTORY SERVICE ---
                .route("inventory-service", r -> r.path("/api/v1/inventory/**")
                        .filters(f -> f.filter(authFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://INVENTORY-SERVICE"))

                .route("inventory-docs", r -> r.path("/INVENTORY-SERVICE/v3/api-docs/**")
                        .filters(f -> f.rewritePath("/INVENTORY-SERVICE/(?<path>.*)", "/${path}"))
                        .uri("lb://INVENTORY-SERVICE"))

                .build();
    }

}
