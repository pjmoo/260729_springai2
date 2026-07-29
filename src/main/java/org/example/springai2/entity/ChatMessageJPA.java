package org.example.springai2.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_message")
@Entity
public class ChatMessageJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String conversationId;
    private String messageType;
    private String content;
    private int seq;

    // import org.springframework.ai.chat.messages.Message;
    public Message toMessage() {
        MessageType messageType = MessageType.valueOf(this.messageType);
        return switch (messageType) {
            case USER -> new UserMessage(this.content);
            case ASSISTANT -> new AssistantMessage(this.content); // AI의 메시지
            case SYSTEM -> new SystemMessage(this.content);
            default -> throw new IllegalArgumentException("지원하지 않는 메시지 타입: %s".formatted(messageType));
        };
    }

    public static ChatMessageJPA fromMessage(Message message, String conversationId, int seq) {
        return ChatMessageJPA.builder()
                .conversationId(conversationId)
                .messageType(message.getMessageType().name())
                .content(message.getText())
                .seq(seq)
                .build();
    }
}
