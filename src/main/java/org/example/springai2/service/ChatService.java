package org.example.springai2.service;

import org.example.springai2.dto.ChatDTO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatModel chatModel; // openAI

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(ChatDTO dto) {
        return chatModel.call(dto.message());
    }
}
