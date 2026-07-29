package org.example.springai2.repository;

import lombok.RequiredArgsConstructor;
import org.example.springai2.entity.ChatMessageMyBatis;
import org.example.springai2.mapper.ChatMessageMapper;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MyBatisChatMemoryRepository implements ChatMemoryRepository {
    private final ChatMessageMapper chatMessageMapper;

    @Override
    public List<String> findConversationIds() {
        return chatMessageMapper.findConversationIds();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return chatMessageMapper.findByConversationId(conversationId)
                .stream()
                .map(ChatMessageMyBatis::toMessage)
                .toList();
    }

    @Override
    @Transactional
    public void saveAll(String conversationId, List<Message> messages) {
        chatMessageMapper.deleteByConversationId(conversationId);

        List<ChatMessageMyBatis> chatMessages = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            ChatMessageMyBatis chatMessage = ChatMessageMyBatis.fromMessage(messages.get(i), conversationId, i);
            chatMessages.add(chatMessage);
        }
        if (chatMessages.isEmpty()) return;

        chatMessageMapper.insertAll(chatMessages);
    }

    @Override
    @Transactional
    public void deleteByConversationId(String conversationId) {
        chatMessageMapper.deleteByConversationId(conversationId);
    }
}
