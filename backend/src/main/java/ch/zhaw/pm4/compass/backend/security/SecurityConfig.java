package ch.zhaw.pm4.compass.backend.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

	@Value("${springdoc.api-docs.enabled}")
	private Boolean swaggerOn;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.authorizeHttpRequests((authorize) -> {
			if (swaggerOn) {
				authorize.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();
			}
			authorize.anyRequest().authenticated();
		}).oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults())).build();
	}
}