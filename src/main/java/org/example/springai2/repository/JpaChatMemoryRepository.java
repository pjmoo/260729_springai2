package org.example.springai2.repository;

import lombok.RequiredArgsConstructor;
import org.example.springai2.entity.ChatMessageJPA;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaChatMemoryRepository implements ChatMemoryRepository {
    //    private final ChatMessageMapper chatMessageMapper;
    private final ChatMemoryJpaRepository repository;

    @Override
    public List<String> findConversationIds() {
        return repository.findConversationIds();
    }

    @Override
    public List<Message> findByConversationId(String conversationId) {
        return repository.findAllByConversationId(conversationId)
                .stream()
                .map(ChatMessageJPA::toMessage)
                .toList();
    }

    @Override
    @Transactional
    public void saveAll(String conversationId, List<Message> messages) {
        repository.deleteAllByConversationId(conversationId);

        List<ChatMessageJPA> chatMessages = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            ChatMessageJPA chatMessage = ChatMessageJPA.fromMessage(messages.get(i), conversationId, i);
            chatMessages.add(chatMessage);
        }
        if (chatMessages.isEmpty()) return;

        repository.saveAll(chatMessages);
    }

    @Override
    @Transactional
    public void deleteByConversationId(String conversationId) {
        repository.deleteAllByConversationId(conversationId);
    }
}
