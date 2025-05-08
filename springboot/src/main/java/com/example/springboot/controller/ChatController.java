package com.example.springboot.controller;

import com.example.springboot.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final WebClient webClient;

    public ChatController() {
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:11434")
                .build();
    }

    @PostMapping("/send")
    public Result sendMessage(@RequestBody Map<String, List<Map<String, String>>> request) {
        try {
            // 获取最后一条用户消息
            String lastUserMessage = request.get("messages").stream()
                    .filter(msg -> "user".equals(msg.get("role")))
                    .map(msg -> msg.get("content"))
                    .reduce((first, second) -> second)
                    .orElse("");

            // 构建Ollama请求
            Map<String, Object> ollamaRequest = Map.of(
                    "model", "deepseek-r1:1.5b",
                    "prompt", lastUserMessage,
                    "stream", false
            );

            // 调用Ollama API
            Map<String, String> response = webClient.post()
                    .uri("/api/generate")
                    .bodyValue(ollamaRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return Result.success(response.get("response"));
        } catch (Exception e) {
            return Result.error("500", "聊天服务暂时不可用");
        }
    }
} 