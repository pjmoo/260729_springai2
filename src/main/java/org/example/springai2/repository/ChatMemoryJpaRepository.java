package org.example.springai2.repository;

import org.example.springai2.entity.ChatMessageJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ChatMemoryJpaRepository extends JpaRepository<ChatMessageJPA, Long> {
    List<ChatMessageJPA> findAllByConversationId(String conversationId);

    @Query("SELECT DISTINCT cm.conversationId FROM ChatMessageJPA cm")
    List<String> findConversationIds();

    // saveAll은 내장으로
    void deleteAllByConversationId(String conversationId);
}
