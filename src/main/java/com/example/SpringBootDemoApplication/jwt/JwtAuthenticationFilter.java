package com.example.SpringBootDemoApplication.jwt;

import com.example.SpringBootDemoApplication.models.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    private static final String REFRESH_TOKEN_ENDPOINT = "/api/auth/refresh-token";

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = extractJwtFromHeader(authorizationHeader);

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                authenticateUser(request, jwt);
            } catch (ExpiredJwtException e) {
                handleExpiredJwtException(request, response, e);
            } catch (Exception e) {
                // handle other exceptions as needed
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromHeader(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }

    private void authenticateUser(HttpServletRequest request, String jwt) {
        String username = jwtUtil.extractUsername(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    private void handleExpiredJwtException(HttpServletRequest request, HttpServletResponse response, ExpiredJwtException e) throws IOException {
        if (!request.getRequestURI().equals(REFRESH_TOKEN_ENDPOINT)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            ErrorResponse errorResponse = createErrorResponse();
            String jsonResponse = convertErrorResponseToJson(errorResponse);

            response.getWriter().write(jsonResponse);
        }
    }

    private ErrorResponse createErrorResponse() {
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setTimestamp(LocalDateTime.now());
        errorResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        errorResponse.setError("Unauthorized");
        errorResponse.setMessage("Token has expired");
        return errorResponse;
    }

    private String convertErrorResponseToJson(ErrorResponse errorResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"));
        return objectMapper.writeValueAsString(errorResponse);
    }
}
