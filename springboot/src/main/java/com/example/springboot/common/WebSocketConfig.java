package com.example.springboot.common;

import com.example.springboot.handler.ChatWebSocketHandler;
import com.example.springboot.interceptor.WebSocketInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

//@Configuration告诉 Spring 这是一个配置类。
@Configuration
//@EnableWebSocket：开启 WebSocket 支持。
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new ChatWebSocketHandler(), "/ws/chat")
                .addInterceptors(new WebSocketInterceptor()) // 加上这个！
                .setAllowedOrigins("*");
    }
//    这里注册了一个 WebSocket 处理器 ChatWebSocketHandler。
//    绑定了一个地址 /ws/chat，前端通过这个地址发起 WebSocket 连接。
//    .setAllowedOrigins("*")：允许所有来源跨域连接。
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

//    这行是为了支持 @ServerEndpoint 注解的 WebSocket（虽然你这里没用到 @ServerEndpoint，
//    但加了这个 Bean，可以避免一些环境兼容性问题，比如 Tomcat/Jetty 的 WebSocket 支持）。
//    简单理解就是：开启底层的 WebSocket 支持。
}