package com.levelgroup.controllers;

import org.springframework.web.bind.annotation.*;
import com.levelgroup.bot.ChatBot;
import java.util.List;

//@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class ChatMessageController {

    private final ChatBot chatBot;

    public ChatMessageController(ChatBot chatBot) {
        this.chatBot = chatBot;
    }

    @GetMapping("/send-message")
    public void sendMessage(
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String messageText
    ) {
        String welcomeMessage = userName + ": " + messageText;
        long chatId = 875602491;
        chatBot.sendMessage(chatId, welcomeMessage);

    }

    @GetMapping("/get-messages")
    public List<String> getMessages() {
        List<String> messages = chatBot.getStoredMessages();
        System.out.println("Fetched messages: " + messages);
        return messages;
    }

}


//        exampleforstudy280623