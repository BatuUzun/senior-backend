package com.foodrecipes.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;

import java.util.Arrays;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("amazon-services", r -> r.path("/amazon-services/**")
                        .uri("lb://amazon-services"))
                .route("email-sender", r -> r.path("/email-sender/**")
                        .uri("lb://email-sender"))
                .route("profile-api", r -> r.path("/profile-api/**")
                        .uri("lb://profile-api"))
                .route("profile-picture-downloader", r -> r.path("/profile-picture-downloader/**")
                        .uri("lb://profile-picture-downloader"))
                .route("search-profile", r -> r.path("/search-profile/**")
                        .uri("lb://search-profile"))
                .route("user-follow", r -> r.path("/user-follow/**")
                        .uri("lb://user-follow"))
                .route("review", r -> r.path("/review/**")
                        .uri("lb://review"))
                .route("like", r -> r.path("/like/**")
                        .uri("lb://like"))
                .route("favorite", r -> r.path("/favorite/**")
                        .uri("lb://favorite"))
                .route("comment", r -> r.path("/comment/**")
                        .uri("lb://comment"))
                .route("credentials", r -> r.path("/credentials/**")
                        .uri("lb://credentials"))
                .route("conversation", r -> r.path("/conversation/**")
                        .uri("lb://conversation"))
                .route("chat-send", r -> r.path("/chat-send/**")
                        .uri("lb://chat-send"))
                .route("chat-search", r -> r.path("/chat-search/**")
                        .uri("lb://chat-search"))
                .route("profile-getter", r -> r.path("/profile-getter/**")
                        .uri("lb://profile-getter"))
                .route(p -> p.path("/chat-send-websocket/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec
                                .rewritePath("/chat-send-websocket/(?<remaining>.*)", "/${remaining}")
                                .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")
                                .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE"))
                                //.filter(customLoggingFilter)) // Add any custom filters if necessary
                        .uri("lb://chat-send-websocket"))
                .route("review-like", r -> r.path("/review-like/**")
                        .uri("lb://review-like"))
                .build();
    }
    
    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Define a global CORS configuration
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("http://localhost:8081", "http://localhost:3000"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        corsConfig.setAllowCredentials(true);

        // Apply the CORS configuration globally
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
