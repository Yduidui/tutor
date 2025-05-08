package com.example.springboot.handler;
/**
 * 处理 WebSocket 通信的逻辑
 */

import com.example.springboot.entity.Message;
import com.example.springboot.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@Component：交给 Spring 管理，方便注入。
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler { //继承了 TextWebSocketHandler，意思是：我们只处理文本消息（TextMessage）

    //记录每个用户的 WebSocket 连接。
    private static final Map<Integer, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    //保存聊天记录到数据库。
    @Resource
    private MessageService messageService;

    //Jackson 的 ObjectMapper，用于 JSON 和对象之间的转换（前端传来的是 JSON，转成 Java 对象）。
    private final ObjectMapper objectMapper = new ObjectMapper();

    //连接建立时：记录用户
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Integer userNo = (Integer) session.getAttributes().get("userNo");
        if (userNo != null) {
            userSessions.put(userNo, session);
            System.out.println("新连接: " + userNo);
        }
    }
    //当一个新用户连接 WebSocket 时，获取 userNo（一般是登录时在 HandshakeInterceptor 里提前放进去的），
    // 然后保存起来。
    //以后可以通过 userNo 找到这个用户的 WebSocketSession，给他发消息。

    /**
     * 接收消息时：保存并转发
     * 收到前端发送的消息（TextMessage）。
     * 用 objectMapper 把 JSON 转成 Java 的 Message 对象。
     * 调 messageService.saveMessage(chatMessage) 把聊天记录存到数据库。
     * 调 sendMessageToUser，把这条消息推送给接收方。
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> messageMap = objectMapper.readValue(message.getPayload(), Map.class);
        String type = (String) messageMap.get("type");
        Integer from = (Integer) session.getAttributes().get("userNo");

        if ("friendRequest".equals(type)) {
            // 处理好友请求
            Integer to = (Integer) messageMap.get("to");
            String status = (String) messageMap.get("status");
            
            // 发送给接收者
            WebSocketSession toSession = userSessions.get(to);
            if (toSession != null && toSession.isOpen()) {
                Map<String, Object> response = Map.of(
                    "type", "friendRequest",
                    "from", from,
                    "status", status
                );
                toSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
            }
        }
    }

    /**
     * 连接关闭时：移除用户
     * 断开连接后，把 userNo 对应的 WebSocketSession 从 userSessions 里面删除，避免内存泄漏。
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Integer userNo = (Integer) session.getAttributes().get("userNo");
        if (userNo != null) {
            userSessions.remove(userNo);
            System.out.println("连接关闭: " + userNo);
        }
    }


    /**
     * 发消息给指定用户
     * 根据 userNo 找到对应的 WebSocketSession。
     * 只要这个连接还开着（isOpen），就发送消息。
     */
    public void sendMessageToUser(Integer userNo, String message) throws IOException {
        WebSocketSession session = userSessions.get(userNo);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        }
    }
}