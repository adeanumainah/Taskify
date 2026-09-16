package com.uas.java1.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.uas.java1.dto.UserKredensialDto;
import com.uas.java1.provider.JwtProvider;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProvider jwtProvider;

    @SuppressWarnings("unchecked")
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtProvider.resolveToken(request);
        if (token != null && jwtProvider.validasiToken(token)) {
            Claims claims = jwtProvider.parseJwtClaims(token);
            String username = claims.get("username", String.class);
            List<String> role = claims.get("role", List.class);

            UserKredensialDto userKredensial = new UserKredensialDto();
            userKredensial.setUserId(Integer.valueOf(claims.getSubject()));
            userKredensial.setUsername(username);

            // UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            //         username,
            //         userKredensial,
            //         role.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    userKredensial,
                    role.stream()
                            .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r) 
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
