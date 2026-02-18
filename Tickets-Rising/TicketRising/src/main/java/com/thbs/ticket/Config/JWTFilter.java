package com.thbs.ticket.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JwtDecoder jwtDecoder;

    public JWTFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Jwt jwt = jwtDecoder.decode(token);

                // Build authorities from multiple possible claims
                Collection<GrantedAuthority> authorities = new ArrayList<>();

                // 1) List claim "roles": ["MANAGER", ...]
                List<String> roles = jwt.getClaimAsStringList("roles");
                if (roles != null) {
                    roles.stream()
                         .map(this::withRolePrefix)
                         .map(SimpleGrantedAuthority::new)
                         .forEach(authorities::add);
                }

                // 2) List claim "authorities": ["ROLE_MANAGER", ...]
                List<String> auths = jwt.getClaimAsStringList("authorities");
                if (auths != null) {
                    auths.stream()
                         .map(this::withRolePrefix)
                         .map(SimpleGrantedAuthority::new)
                         .forEach(authorities::add);
                }

                // 3) Single claim "role": "MANAGER"
                String singleRole = jwt.getClaimAsString("role");
                if (singleRole != null && !singleRole.isBlank()) {
                    authorities.add(new SimpleGrantedAuthority(withRolePrefix(singleRole)));
                }

                JwtAuthenticationToken authentication =
                        new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // ===== DEBUG LOGS =====
                Authentication a = SecurityContextHolder.getContext().getAuthentication();
                System.out.println(
                    "[Ticket JWTFilter] user=" + (a != null ? a.getName() : null)
                    + " authorities=" + (a != null ? a.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList() : null)
                    + " method=" + request.getMethod()
                    + " path=" + request.getRequestURI()
                );
                System.out.println("[Ticket JWTFilter] claims=" + jwt.getClaims());
                // ======================

            } catch (JwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.getWriter().write("{\"error\":\"invalid_token\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /** Ensures "ROLE_" prefix exactly once. */
    private String withRolePrefix(String value) {
        if (value == null) return null;
        String v = value.trim();
        return v.startsWith("ROLE_") ? v : "ROLE_" + v;
    }
}