package com.frauddetection.gateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.Route;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class ApiVersionConfigTest {

    private final ApiVersionConfig config = new ApiVersionConfig();

    @Test
    void versionedRoutes_ShouldCreateThreeRoutes() {
        RouteLocator routeLocator = config.versionedRoutes(new RouteLocatorBuilder());
        Flux<Route> routes = routeLocator.getRoutes();

        StepVerifier.create(routes)
            .expectNextCount(3) // v1, v2, and default routes
            .verifyComplete();
    }

    @Test
    void versionedRoutes_ShouldConfigureV1Route() {
        RouteLocator routeLocator = config.versionedRoutes(new RouteLocatorBuilder());
        Flux<Route> routes = routeLocator.getRoutes();

        StepVerifier.create(routes)
            .expectNextMatches(route -> 
                route.getId().equals("v1_route") &&
                route.getPredicate().test(MockServerWebExchange.from(
                    MockServerHttpRequest.get("/api/v1/test").build()
                ))
            )
            .thenCancel()
            .verify();
    }

    @Test
    void versionedRoutes_ShouldConfigureV2Route() {
        RouteLocator routeLocator = config.versionedRoutes(new RouteLocatorBuilder());
        Flux<Route> routes = routeLocator.getRoutes();

        StepVerifier.create(routes)
            .expectNextMatches(route -> 
                route.getId().equals("v2_route") &&
                route.getPredicate().test(MockServerWebExchange.from(
                    MockServerHttpRequest.get("/api/v2/test").build()
                ))
            )
            .thenCancel()
            .verify();
    }

    @Test
    void versionedRoutes_ShouldConfigureDefaultRoute() {
        RouteLocator routeLocator = config.versionedRoutes(new RouteLocatorBuilder());
        Flux<Route> routes = routeLocator.getRoutes();

        StepVerifier.create(routes)
            .expectNextMatches(route -> 
                route.getId().equals("default_route") &&
                route.getPredicate().test(MockServerWebExchange.from(
                    MockServerHttpRequest.get("/api/test").build()
                ))
            )
            .thenCancel()
            .verify();
    }

    @Test
    void versionHeaderFilter_ShouldExist() {
        assertNotNull(config.versionHeaderFilter());
    }
}