package com.bragdev.fraud.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfiguration {

    @Value("${spring.security.basic.users[0].username:api-client}")
    private String apiUsername;

    @Value("${spring.security.basic.users[0].password:changeme}")
    private String apiPassword;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf().disable()
            .authorizeExchange()
                .pathMatchers("/actuator/**", "/favicon.ico").permitAll()
                .anyExchange().authenticated()
            .and()
            .httpBasic()
            .and()
            .build();
    }

    @Bean
    public MapReactiveUserDetailsService userDetailsService(BCryptPasswordEncoder passwordEncoder) {
        return new MapReactiveUserDetailsService(
            User.builder()
                .username(apiUsername)
                .password(passwordEncoder.encode(apiPassword))
                .roles("API_USER")
                .build()
        );
    }
}