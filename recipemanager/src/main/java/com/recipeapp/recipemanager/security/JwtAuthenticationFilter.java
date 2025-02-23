package com.recipeapp.recipemanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.javapoet.ClassName;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ClassName.class);

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException{

        logger.info("JwtAuthenticationFilter is running for request: {}", request.getRequestURI());

        Collections.list(request.getHeaderNames()).forEach(headerName ->
                logger.info("🔍 Header: {} = {}", headerName, request.getHeader(headerName)));

        //Extract token from Authorization header
        String token = getTokenFromRequest(request);
        logger.info("Checking JWT Token: {}", token);

        if(token != null && jwtTokenProvider.validateToken(token)){
            String email = jwtTokenProvider.getEmailFromToken(token);
            logger.info("Extracted Email from token: {}", email);


            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

            String authorities = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", "));

            // Log user details separately to avoid issues
            logger.info("User found: {}",  userDetails.getUsername());
            logger.info("Roles: {}", authorities); // Explicitly convert to string


            //if(userDetails != null){

            JwtAuthenticationToken authentication =
                    new JwtAuthenticationToken(userDetails, token, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("User authenticated successfully: {}", email);
        } else {
            logger.warn("Invalid or missing JWT token");
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");

        logger.info(" Raw Authorization Header: {}", bearerToken);

        if(bearerToken != null && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
