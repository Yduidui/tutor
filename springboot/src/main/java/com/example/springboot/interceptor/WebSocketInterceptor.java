package com.example.springboot.interceptor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        // 提取 URL 参数中的 userNo
        String query = request.getURI().getQuery(); // userNo=123
        if (query != null && query.contains("userNo")) {
            String[] parts = query.split("=");
            Integer userNo = Integer.valueOf(parts[1]);
            attributes.put("userNo", userNo); // 放入 attributes
        }
        return true; // 继续握手
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {
        // 握手完成后可选的处理
    }
}
