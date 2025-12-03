package com.metasoft.restyle.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.metasoft.restyle.platform.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.metasoft.restyle.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Bearer Authorization Request Filter.
 * <p>
 * This class is responsible for filtering requests and setting the user authentication.
 * It extends the OncePerRequestFilter class.
 * </p>
 * @see OncePerRequestFilter
 */
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BearerAuthorizationRequestFilter.class);
    private final BearerTokenService tokenService;

    @Qualifier("defaultUserDetailsService")
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(BearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            LOGGER.info("Authorization header: {}", authHeader != null ? "Present" : "Missing");
            LOGGER.info("Request URI: {}", request.getRequestURI());
            
            String token = tokenService.getBearerTokenFrom(request);
            LOGGER.info("Extracted token: {}", token != null ? "Present (length: " + token.length() + ")" : "Null");
            
            if (token != null) {
                boolean isValid = tokenService.validateToken(token);
                LOGGER.info("Token validation result: {} for URI: {}", isValid, request.getRequestURI());
                
                if (isValid) {
                    String username = tokenService.getUsernameFromToken(token);
                    LOGGER.info("Extracted username from token: {}", username);
                    
                    try {
                        var userDetails = userDetailsService.loadUserByUsername(username);
                        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
                        LOGGER.info("Authentication set successfully for user: {} on URI: {}", username, request.getRequestURI());
                    } catch (Exception e) {
                        LOGGER.error("Error loading user details for username '{}' on URI '{}': {}", username, request.getRequestURI(), e.getMessage(), e);
                        // No continuamos si no podemos cargar el usuario - Spring Security manejará el 401
                    }
                } else {
                    LOGGER.warn("Token validation failed for request to: {}", request.getRequestURI());
                }
            } else {
                LOGGER.warn("No token found in request to: {}", request.getRequestURI());
            }

        } catch (Exception e) {
            LOGGER.error("Cannot set user authentication for URI '{}': {}", request.getRequestURI(), e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
