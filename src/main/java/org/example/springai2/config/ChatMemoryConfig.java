package org.example.springai2.config;

import org.example.springai2.repository.JpaChatMemoryRepository;
import org.example.springai2.repository.MyBatisChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatMemoryConfig {

    private ChatMemory messageWindow(ChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(3)
                .build();
    }

    //    org.springframework.ai.chat.model
    private ChatClient chatClientWith(ChatModel chatModel, ChatMemory memory) {
        return ChatClient.builder(chatModel)
//                .defaultSystem()
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }

    // inmemory
    @Bean
    public ChatMemory inMemoryChatMemory() {
        return messageWindow(new InMemoryChatMemoryRepository());
    }

    @Bean
    public ChatClient inMemoryChatClient(
            ChatModel chatModel, // openAI 기본만 사용
            @Qualifier("inMemoryChatMemory") ChatMemory memory) {
        return chatClientWith(chatModel, memory);
    }

    @Bean
    public ChatMemory mybatisChatMemory(MyBatisChatMemoryRepository repository) {
        return messageWindow(repository);
    }

    @Bean
    public ChatClient mybatisChatClient(
            ChatModel chatModel,
            @Qualifier("mybatisChatMemory") ChatMemory memory) {
        return chatClientWith(chatModel, memory);
    }

    @Bean
    public ChatMemory jpaChatMemory(JpaChatMemoryRepository repository) {
        return messageWindow(repository);
    }

    @Bean
    public ChatClient jpaChatClient(
            ChatModel chatModel,
            @Qualifier("jpaChatMemory") ChatMemory memory) {
        return chatClientWith(chatModel, memory);
    }
}
