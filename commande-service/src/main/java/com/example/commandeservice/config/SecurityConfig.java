package com.example.commandeservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
<<<<<<< Updated upstream
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
=======
import org.springframework.security.web.SecurityFilterChain;

@Configuration
>>>>>>> Stashed changes
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
<<<<<<< Updated upstream
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()   // Tout accessible pour le moment
            );
=======
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/commandes/test").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic();
>>>>>>> Stashed changes

        return http.build();
    }
}