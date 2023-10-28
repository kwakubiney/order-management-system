package com.example.ordermanagementsystem.security.jwt;

import com.example.ordermanagementsystem.exception.CustomGraphQLException;
import com.example.ordermanagementsystem.security.TokenPayload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtUtils tokenUtils;

    public JWTAuthorizationFilter(AuthenticationManager authManager, JwtUtils tokenUtils) {
        super(authManager);
        this.tokenUtils = tokenUtils;
    }

    //TODO: Handle exceptions which occur here
    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader("authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (token != null) {
            TokenPayload tokenPayload = tokenUtils.decodeToken(token);

            if (tokenPayload.getEmail() != null) {
                return new UsernamePasswordAuthenticationToken(tokenPayload.getEmail(), null, Collections.singletonList(tokenPayload.getRole()));
            }else{
                throw new CustomGraphQLException("User is missing key details in token", 401);
            }
        }
        throw new CustomGraphQLException("Token is missing from request headers", 401);
    }
}