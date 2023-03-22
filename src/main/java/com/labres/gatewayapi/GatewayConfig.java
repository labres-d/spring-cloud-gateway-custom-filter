package com.labres.gatewayapi;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, AuthenticationFilter filter) {
        return builder.routes()
                .route("route1", r -> r.path("/**").filters(f -> f.filter(filter)
                        .requestRateLimiter(config -> config.setRateLimiter(customRateLimiter()).setDenyEmptyKey(true)
                                .setKeyResolver(customKeyResolver()))).uri("http://localhost:8081/"))
                .build();
    }

    @Bean
    public KeyResolver customKeyResolver() {
        return exchange -> {
            List<String> headerId = exchange.getRequest().getHeaders().get("id");
            return headerId != null ? Mono.just(headerId.get(0)) : Mono.empty();
        };
    }

    @Bean
    @Primary
    public RateLimiter customRateLimiter() {
        return new RedisRateLimiter(2, 2, 1);
    }
}