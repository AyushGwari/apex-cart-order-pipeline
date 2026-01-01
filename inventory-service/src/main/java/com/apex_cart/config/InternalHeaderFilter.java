package com.apex_cart.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
public class InternalHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String username = request.getHeader("loggedInUser");
        String rolesHeader = request.getHeader("loggedInUserRoles");
        List<SimpleGrantedAuthority> authorities = Collections.emptyList();

            if(username == null || username.isBlank()){
                log.warn("Blocked request: Missing 'loggedInUser' header. Source: {}", request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: Request must originate from API Gateway");
                return;
            }
        if (rolesHeader != null && !rolesHeader.isBlank()) {
            authorities = Arrays.stream(rolesHeader.split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username,null, authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
        log.debug("Authenticated internal request for user: {}", username);
        filterChain.doFilter(request,response);
    }
}
