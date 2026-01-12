package com.class_manager.user_auth_service.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if ("true".equals(request.getHeader("X-Internal-Call"))) {

            UsernamePasswordAuthenticationToken internalAuth =
                    new UsernamePasswordAuthenticationToken(
                            "INTERNAL_SERVICE",
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
                    );

            SecurityContextHolder.getContext().setAuthentication(internalAuth);
            filterChain.doFilter(request, response);
            return;
        }
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String jwt = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("token")) {
                    jwt = cookie.getValue();
                }
            }
        }

        if (jwt == null) {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
        }
        //   if(authHeader == null || !authHeader.startsWith("Bearer ")){
        //       filterChain.doFilter(request,response);
        //        return;
        //  }
        //  jwt =authHeader.substring(7);
        if (jwt != null) {
            String userEmail = jwtService.extractUsername(jwt);
            System.out.println("JWT correspond à l'utilisateur: " + userEmail);
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication définie : " + SecurityContextHolder.getContext().getAuthentication());
                    System.out.println("Authorities : " + userDetails.getAuthorities());
                }
            }
        }
        filterChain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        String internalHeader = request.getHeader("X-Internal-Call");
        return path.startsWith("/api/v1/auth/") || "true".equals(internalHeader);

    }

}
