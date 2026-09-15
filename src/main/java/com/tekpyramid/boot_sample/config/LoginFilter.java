package com.tekpyramid.boot_sample.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoginFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(LoginFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Request log
        log.info("========== REQUEST ==========");
        log.info("Method : {}", request.getMethod());
        log.info("URL    : {}", request.getRequestURI());

        if (request.getRequestURI().equals("/login")) {
            log.info("Login request received");
        }

        log.info("=============================");

        // Continue request
        filterChain.doFilter(request, response);

        // Response log
        log.info("========== RESPONSE =========");
        log.info("Status : {}", response.getStatus());
        log.info("URL    : {}", request.getRequestURI());
        log.info("=============================");
    }
}