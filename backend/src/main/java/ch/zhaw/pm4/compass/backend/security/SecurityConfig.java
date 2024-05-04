package ch.zhaw.pm4.compass.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
          .authorizeHttpRequests((authorize) -> authorize
            .anyRequest().permitAll()
          )
          .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(withDefaults())
          )
          .build();
    }
}