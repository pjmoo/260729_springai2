package org.example.springai2.service;

import lombok.RequiredArgsConstructor;
import org.example.springai2.dto.ChatDTO;
import org.example.springai2.repository.MyBatisChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService2 {
//    @Qualifier("inMemoryChatClient") // @RequiredArgsConstructor <- lombok.config

    //    @Qualifier("mybatisChatClient")
    @Qualifier("jpaChatClient")
    private final ChatClient chatClient;
    //    @Qualifier("inMemoryChatMemory")
//    private final ChatMemory chatMemory;
    private final MyBatisChatMemoryRepository chatMemoryRepository;

    public String chat(ChatDTO dto) {
        return chatClient.prompt().system("친절하게 50자 이내로 한글로 대답")
                .options(OpenAiChatOptions.builder()
                        .extraBody(Map.of("include_reasoning", false)))
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, dto.conversationId()))
                .user(dto.message())
                .call().content();
    }

    // org.springframework.ai.chat.messages.Message;
    public List<Message> getHistory(String conversationId) {
//        return chatMemory.get(conversationId);
        return chatMemoryRepository.findByConversationId(conversationId);
    }
}
