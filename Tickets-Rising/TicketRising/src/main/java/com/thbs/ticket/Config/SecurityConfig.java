package com.thbs.ticket.Config;

import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

	// HS256 shared secret (MUST match the Auth/User service signing secret)
	@Value("${app.jwt.hmac-secret}")
	private String secret;

	@Bean
	public JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"))
				.build();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JWTFilter jwtFilter) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						
						.requestMatchers("/actuator/health").permitAll()
						
						.requestMatchers(HttpMethod.POST, "/api/tickets").hasRole("CUSTOMER")
						.requestMatchers(HttpMethod.GET, "/api/tickets").hasRole("MANAGER")
						
						// customers
						.requestMatchers(HttpMethod.GET, "/api/tickets/**").authenticated()
						// Agent actions
						.requestMatchers(HttpMethod.PATCH, "/api/tickets/*/assign").hasRole("MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/tickets/tickets/*/requestresolution").hasRole("AGENT")
						// Manager approvals
						.requestMatchers(HttpMethod.PATCH, "/api/tickets/*/resolve").hasRole("MANAGER")
						.requestMatchers(HttpMethod.PATCH, "/api/tickets/*/reject").hasRole("MANAGER")
						

						.anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public AccessDeniedHandler accessDeniedLogger() {
		return (request, response, ex) -> {
			var auth = SecurityContextHolder.getContext().getAuthentication();
			String principal = (auth != null ? auth.getName() : "anonymous");
			String authorities = (auth != null ? auth.getAuthorities().stream().map(GrantedAuthority::getAuthority)
					.collect(Collectors.joining(",")) : "none");

			System.err.println("=== ACCESS DENIED ===");
			System.err.println("Path: " + request.getMethod() + " " + request.getRequestURI());
			System.err.println("User: " + principal + " / Authorities: [" + authorities + "]");
			System.err.println("Reason: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
			ex.printStackTrace(); 

			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\":\"access_denied\"}");
		};
	}
}
