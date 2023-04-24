package com.example.SpringBootDemoApplication.sockets;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/send-message")
    @SendTo("/topic/messages")
    public String processMessage(String message) {
        // Process the received message and return the response
        return "Received message: " + message;
    }
}
