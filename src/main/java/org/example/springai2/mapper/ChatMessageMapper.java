package org.example.springai2.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.example.springai2.entity.ChatMessageMyBatis;

import java.util.List;

@Mapper
public interface ChatMessageMapper {
    // 저장된 대화 ID 목록
    List<String> findConversationIds();

    // 한 대화의 멧지 전체를 순서대로
    List<ChatMessageMyBatis> findByConversationId(String conversationId);

    void insertAll(List<ChatMessageMyBatis> messages);

    void deleteByConversationId(String conversationId);
}
