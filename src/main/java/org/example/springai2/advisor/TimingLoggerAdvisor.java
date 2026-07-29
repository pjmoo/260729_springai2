package org.example.springai2.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

@Slf4j // logger를 주입해줌 (log)
public class TimingLoggerAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        long startTime = System.currentTimeMillis();
        log.info("[TimingLoggerAdvisor] 시작 : prompt: {}",
                request.prompt().getContents());
        // NextCall 생략하면 무슨 일 일어난지 보기
        ChatClientResponse response = chain.nextCall(request); // 이거 없으면 다음으로 진행 안되고 콜 X
        long endTime = System.currentTimeMillis();
        log.info("[TimingLoggerAdvisor] 종료 : {} ms", endTime - startTime);
        return response;
    }

    @Override
    public String getName() {
        return "TimingLoggerAdvisor";
    }

    // Order 조정해서 결과 보기
    @Override
    public int getOrder() {
        return 0; // 우선순위 -> 값이 높을 수록 내부에서 실행됨
        // 값이 작을 수록 체인 바깥쪽
    }
}
