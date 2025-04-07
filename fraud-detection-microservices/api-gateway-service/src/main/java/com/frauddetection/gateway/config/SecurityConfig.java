package com.frauddetection.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(
            ServerHttpSecurity http,
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        
        // Configure logout handler
        ServerLogoutSuccessHandler logoutSuccessHandler = 
            new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);

        return http
            .csrf().disable()
            .authorizeExchange()
                .pathMatchers("/actuator/**").permitAll()
                .pathMatchers("/favicon.ico").permitAll()
                .anyExchange().authenticated()
            .and()
            .oauth2Login()
                .and()
            .oauth2ResourceServer()
                .jwt()
                .and()
            .and()
            .logout()
                .logoutSuccessHandler(logoutSuccessHandler)
            .and()
            // Enable security context propagation
            .securityContextRepository(new WebSessionServerSecurityContextRepository())
            .build();
    }

    @Bean
    public WebSessionServerSecurityContextRepository securityContextRepository() {
        return new WebSessionServerSecurityContextRepository();
    }
}