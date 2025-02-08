package com.foodrecipes.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
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
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Amazon Services CORS Configuration
        CorsConfiguration amazonServicesCors = new CorsConfiguration();
        amazonServicesCors.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        amazonServicesCors.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        amazonServicesCors.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        amazonServicesCors.setAllowCredentials(true);
        source.registerCorsConfiguration("/amazon-services/**", amazonServicesCors);

        // Email Sender CORS Configuration
        CorsConfiguration emailSenderCors = new CorsConfiguration();
        emailSenderCors.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        emailSenderCors.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        emailSenderCors.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        emailSenderCors.setAllowCredentials(true);
        source.registerCorsConfiguration("/email-sender/**", emailSenderCors);

        // Profile API CORS Configuration
        CorsConfiguration profileApiCors = new CorsConfiguration();
        profileApiCors.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        profileApiCors.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        profileApiCors.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        profileApiCors.setAllowCredentials(true);
        source.registerCorsConfiguration("/profile-api/**", profileApiCors);

        // Profile Picture Downloader CORS Configuration
        CorsConfiguration profilePictureDownloaderCors = new CorsConfiguration();
        profilePictureDownloaderCors.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        profilePictureDownloaderCors.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        profilePictureDownloaderCors.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        profilePictureDownloaderCors.setAllowCredentials(true);
        source.registerCorsConfiguration("/profile-picture-downloader/**", profilePictureDownloaderCors);

        // Search Profile CORS Configuration
        CorsConfiguration searchProfileCors = new CorsConfiguration();
        searchProfileCors.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        searchProfileCors.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS"));
        searchProfileCors.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        searchProfileCors.setAllowCredentials(true);
        source.registerCorsConfiguration("/search-profile/**", searchProfileCors);
        
        CorsConfiguration userFollow = new CorsConfiguration();
        userFollow.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        userFollow.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        userFollow.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        userFollow.setAllowCredentials(true);
        source.registerCorsConfiguration("/user-follow/**", userFollow);
        
        CorsConfiguration review = new CorsConfiguration();
        review.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        review.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        review.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        review.setAllowCredentials(true);
        source.registerCorsConfiguration("/review/**", review);
        
        CorsConfiguration like = new CorsConfiguration();
        like.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        like.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        like.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        like.setAllowCredentials(true);
        source.registerCorsConfiguration("/like/**", like);
        
        CorsConfiguration favorite = new CorsConfiguration();
        favorite.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        favorite.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        favorite.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        favorite.setAllowCredentials(true);
        source.registerCorsConfiguration("/favorite/**", favorite);
        
        CorsConfiguration comment = new CorsConfiguration();
        comment.setAllowedOrigins(Arrays.asList("http://localhost:8081", "https://your-frontend-url.com"));
        comment.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        comment.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        comment.setAllowCredentials(true);
        source.registerCorsConfiguration("/comment/**", comment);

        return new CorsWebFilter(source);
    }
}
