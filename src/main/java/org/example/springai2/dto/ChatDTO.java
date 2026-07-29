package org.example.springai2.dto;

public record ChatDTO(String message, String conversationId) {
    public ChatDTO withID(String id) {
        return new ChatDTO(message, id);
    }
}
