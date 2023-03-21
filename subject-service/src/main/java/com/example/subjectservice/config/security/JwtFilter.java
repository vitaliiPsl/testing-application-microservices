package com.example.subjectservice.config.security;

import com.example.subjectservice.client.AuthClient;
import com.example.subjectservice.payload.ApiErrorDto;
import com.example.subjectservice.payload.auth.TokenExchangeRequestDto;
import com.example.subjectservice.payload.auth.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {
    private final AuthClient authClient;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("Authorization: {}", authorization);

        if (authorization == null || authorization.isBlank() || !authorization.startsWith("Bearer ")) {
            log.debug("Unauthorized: {}", authorization);
            filterChain.doFilter(req, res);
            return;
        }

        // fetch user details
        verifyToken(req, res, filterChain, authorization);
    }

    private void verifyToken(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain, String authorization) throws IOException {
        log.debug("Verify authorization: {}", authorization);

        String token = authorization.replace("Bearer ", "");
        try {
            UserDto userDto = authClient.exchangeToken(new TokenExchangeRequestDto(token));
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + userDto.getRole()));

            PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(userDto, token, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(req, res);
        } catch (Exception e) {
            log.error("Jwt token failed verification: {}", token, e);
            writeErrorResponse(res);
        }
    }

    private void writeErrorResponse(HttpServletResponse response) throws IOException {
        ApiErrorDto apiError = new ApiErrorDto(HttpStatus.UNAUTHORIZED, "Invalid token");

        String responseBody = objectMapper.writeValueAsString(apiError);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setContentLength(responseBody.length());

        response.getWriter().print(responseBody);
        response.getWriter().flush();
    }

}
