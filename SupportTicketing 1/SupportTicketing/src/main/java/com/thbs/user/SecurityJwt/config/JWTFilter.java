package com.thbs.user.SecurityJwt.config;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thbs.user.Service.JWTService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JWTFilter(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip token processing for open endpoints
        return pathMatcher.match("/api/user/register", path)
            || pathMatcher.match("/api/user/login", path)
            || pathMatcher.match("/actuator/health", path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            // 1) Validate token (signature + expiry)
            if (!jwtService.isValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2) Extract subject (username) and skip if already authenticated
            String username = jwtService.extractUserName(token);
            if (username == null || username.isBlank()
                || SecurityContextHolder.getContext().getAuthentication() != null) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3) Extract raw roles (e.g., MANAGER) and build ROLE_ authorities
            List<String> roles = jwtService.extractRoles(token);
            List<GrantedAuthority> authorities = roles.stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());

            // 4) Build Authentication and set context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException ex) {
            // Optionally: response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
            // return;
        }

        filterChain.doFilter(request, response);
    }
}
