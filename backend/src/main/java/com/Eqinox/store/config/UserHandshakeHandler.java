package com.Eqinox.store.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class UserHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
            ServerHttpRequest request,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {

        Integer userId = (Integer) attributes.get("userId");

        if (userId == null) {
            return null;
        }

        // 🔑 THIS is what convertAndSendToUser() matches against
        return () -> userId.toString();
    }
}
