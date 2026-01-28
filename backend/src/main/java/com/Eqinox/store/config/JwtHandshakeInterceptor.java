package com.Eqinox.store.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.Eqinox.store.security.JwtService;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest servletRequest) {

            // ✅ 1️⃣ FIRST: try query param (SockJS support)
            String token = servletRequest
                    .getServletRequest()
                    .getParameter("token");

            // ✅ 2️⃣ FALLBACK: try Authorization header (native WS)
            if (token == null) {
                String authHeader = servletRequest
                        .getServletRequest()
                        .getHeader("Authorization");

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }

            // ✅ 3️⃣ Resolve userId
            if (token != null) {
                Integer userId = jwtService.extractUserId(token);
                attributes.put("userId", userId);
            }
        }

        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // no-op
    }
}
