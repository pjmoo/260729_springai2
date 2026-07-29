package org.example.springai2.service;

import org.example.springai2.dto.ChatDTO;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    private final ChatModel chatModel; // openAI
    private final ChatClient chatClient;

    public ChatService(ChatModel chatModel) {
        this.chatModel = chatModel;
        String systemTemplate = "한글로 된 반말. 50자를 넘기지 않음. {persona}";
        String persona = "상당히 예의없고 건방짐.";
        this.chatClient = ChatClient.builder(chatModel)
                .defaultSystem(s -> s.text(systemTemplate).param("persona", persona))
                .build();
    }

    public String chat(ChatDTO dto) {
//        PromptTemplate promptTemplate = new PromptTemplate("%s 내가 무슨 질문을 하든지 가장 관련 있는 포켓몬으로 답변해줘".formatted(dto.message()));
//        PromptTemplate promptTemplate = new PromptTemplate("<질문>{message}</질문> 내가 무슨 질문을 하든지 가장 관련 있는 포켓몬으로 답변해줘. 가능한 한글을 써서");
//        return chatModel.call(dto.message());
//        return chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
        // Map.of(첫번째키, 첫번째값, 두번째키, 두번째값...)
//        return chatModel.call(promptTemplate.create(Map.of("message", dto.message()))).getResult().getOutput().getText();
//        SystemPromptTemplate
        String template = "<메시지>{message}</메시지>와 관련된 {category}을 5종 추천해줘. 가능한 한글로.";
        return chatClient.prompt()
//                .system()
                .user(
                        u -> u.text(template)
                                .param("message", dto.message())
                                .param("category", "디지몬"))
                .call().content();
    }
}
