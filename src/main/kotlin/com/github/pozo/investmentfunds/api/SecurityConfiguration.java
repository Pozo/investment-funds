package com.github.pozo.investmentfunds.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(HttpMethod.GET, "/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/funds").permitAll()
                        .requestMatchers(HttpMethod.POST, "/funds").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rates/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/rates/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/sheets/rates/*").permitAll()
                        .anyRequest().denyAll()
        ).csrf(AbstractHttpConfigurer::disable); // https://stackoverflow.com/a/51088555
        return http.build();
    }
}
