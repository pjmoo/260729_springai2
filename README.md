# 🍃 Spring AI 2.0 실습 가이드 (Spring Boot 4.x + Groq AI)

Spring Boot와 **Spring AI** 라이브러리를 활용하여 Groq 클라우드 API를 연동하고, 정형 데이터 출력, Advisor 활용, 그리고 데이터베이스(MyBatis / JPA)를 이용한 대화 내용 저장(Chat Memory)까지 구현한 실습 안내서입니다. 처음 공부하는 사람도 쉽게 따라 할 수 있도록 단계별로 구성되어 있습니다.

---

## 📂 프로젝트 구조 (Directory Structure)

실습에 사용된 핵심 소스 코드 파일들의 위치와 역할입니다:

*   **`src/main/java/org/example/springai2/`**
    *   [ServletInitializer.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/ServletInitializer.java) / [Springai2Application.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/Springai2Application.java): 애플리케이션 시작점
    *   [advisor/TimingLoggerAdvisor.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/advisor/TimingLoggerAdvisor.java): AI 호출 시간 측정을 위한 커스텀 어드바이저
    *   [config/ChatMemoryConfig.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/config/ChatMemoryConfig.java): 인메모리, MyBatis, JPA 대화 메모리 및 ChatClient 빈(Bean) 설정
    *   [controller/MainController.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/controller/MainController.java): HTTP 요청 처리 컨트롤러 (JSP 뷰 연동)
    *   [dto/](file:///C:/workspace/springai2/src/main/java/org/example/springai2/dto/)
        *   [ChatDTO.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/dto/ChatDTO.java): 채팅 메시지 전송용 데이터 모델
        *   [FoodDTO.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/dto/FoodDTO.java): 구조화된 음식 추천 결과를 담는 DTO
    *   [entity/](file:///C:/workspace/springai2/src/main/java/org/example/springai2/entity/)
        *   [ChatMessageMyBatis.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/entity/ChatMessageMyBatis.java): MyBatis용 대화 기록 엔티티
        *   [ChatMessageJPA.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/entity/ChatMessageJPA.java): JPA용 대화 기록 엔티티
    *   [mapper/ChatMessageMapper.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/mapper/ChatMessageMapper.java): MyBatis 매퍼 인터페이스
    *   [repository/](file:///C:/workspace/springai2/src/main/java/org/example/springai2/repository/)
        *   [ChatMemoryJpaRepository.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/repository/ChatMemoryJpaRepository.java): Spring Data JPA 인터페이스
        *   [JpaChatMemoryRepository.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/repository/JpaChatMemoryRepository.java): JPA 기반 ChatMemoryRepository 구현체
        *   [MyBatisChatMemoryRepository.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/repository/MyBatisChatMemoryRepository.java): MyBatis 기반 ChatMemoryRepository 구현체
    *   [service/](file:///C:/workspace/springai2/src/main/java/org/example/springai2/service/)
        *   [ChatService.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/service/ChatService.java): 기초적인 AI 연동, 구조화 출력, 기본 어드바이저 테스트 서비스
        *   [ChatService2.java](file:///C:/workspace/springai2/src/main/java/org/example/springai2/service/ChatService2.java): 대화 기록이 연동된 대화형 서비스

---

## 🛠️ 1단계. Spring AI와 Groq API 연동하기

### 1. 의존성 설정 (`pom.xml`)
Spring AI를 사용하기 위해 OpenAI 라이브러리를 추가하고, Groq API가 OpenAI 호환 규격을 사용하므로 base-url을 변경하여 연동합니다.
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

### 2. 설정 파일 작성 (`application-dev.properties` & `.env.dev`)
API Key와 접속 정보를 설정합니다. API 키는 보안을 위해 `.env.dev` 파일에 따로 분리합니다.

*   **`application-dev.properties`**
    ```properties
    # .env.dev 환경 변수 자동 임포트 설정
    spring.config.import=optional:file:.env.dev[.properties]

    # Groq OpenAI 호환 주소로 설정
    spring.ai.openai.api-key=${GROQ_API_KEY}
    spring.ai.openai.base-url=https://api.groq.com/openai/v1
    spring.ai.openai.chat.model=openai/gpt-oss-120b
    ```
*   **`.env.dev`**
    ```properties
    GROQ_API_KEY=gsk_your_real_api_key_here
    ```

---

## 💡 2단계. 기본적인 AI 요청 및 Prompt 다루기

### ChatModel vs ChatClient
*   **`ChatModel`**: LLM 엔진과 직접 통신하는 로우레벨 인터페이스입니다.
*   **`ChatClient`**: Spring AI 2.0에서 권장하는 하이레벨 유틸리티 인터페이스입니다. 빌더 패턴과 Fluent API(메서드 체이닝)를 지원해 템플릿 처리, 어드바이저 추가, 결과 타입 변환 등을 우아하게 작성할 수 있습니다.

```java
// ChatClient 빌드 및 PromptTemplate을 활용한 기본 호출 예시
String template = "<메시지>{message}</메시지>와 관련된 {category}을 5종 추천해줘.";
String response = chatClient.prompt()
    .user(u -> u.text(template)
            .param("message", dto.message())
            .param("category", "디지몬"))
    .call().content();
```

---

## 🧩 3단계. 구조화된 데이터 출력 (Structured Output)

AI의 단순 텍스트 답변을 Java 객체(DTO)나 List, Map 등으로 자동 파싱하여 받습니다.

### 1. DTO 객체로 직접 파싱 (`entity()` 메서드 사용)
```java
// DTO 클래스 매핑 (자동 BeanOutputConverter 사용)
public FoodDTO recommendFood(ChatDTO dto) {
    return chatClient.prompt()
            .user(u -> u.text("{message}에 관련된 음식을 추천해줘").param("message", dto.message()))
            .call()
            .entity(FoodDTO.class); // FoodDTO 객체 형태로 파싱되어 반환됨
}
```

### 2. 리스트(List)나 맵(Map) 형식 변환
```java
// 1) List 형식으로 변환
List<String> list = chatClient.prompt()
    .user(u -> u.text("{message} 관련 음식 10개 추천").param("message", dto.message()))
    .call()
    .entity(new ListOutputConverter(new DefaultConversionService()));

// 2) Map 형식으로 변환
Map<String, Object> map = chatClient.prompt()
    .user(u -> u.text("{message} 관련 음식 10개 추천").param("message", dto.message()))
    .call()
    .entity(new ParameterizedTypeReference<Map<String, Object>>() {});
```

### 3. 수동 변환 (Manual Conversion)
LLM에게 JSON 형식을 강제하기 위한 포맷 지시문을 직접 Prompt에 포함시키고 응답받은 문자열을 파싱합니다.
```java
BeanOutputConverter<FoodDTO> converter = new BeanOutputConverter<>(FoodDTO.class);
String formatInstruction = converter.getFormat(); // JSON 포맷 가이드라인 문자열 자동 생성

String rawResult = chatClient.prompt()
        .user(u -> u.text("{message}에 관련된 음식을 추천해줘.\n{format}")
                .param("message", dto.message())
                .param("format", formatInstruction))
        .call().content();

FoodDTO food = converter.convert(rawResult); // JSON 문자열 -> Java DTO 변환
```

---

## 🛡️ 4단계. Advisor(어드바이저)로 공통 기능 처리

**Advisor**는 AI의 요청(Request)과 응답(Response) 전후에 작동하여 로깅, 토큰 측정, 대화 기록 연동 등의 공통 관심사를 처리하는 일종의 인터셉터(Interceptor)입니다.

### 1. 내장 어드바이저 (`SimpleLoggerAdvisor`)
요청한 Prompt 정보와 전달받은 결과 텍스트를 디버그 로그에 자동으로 기록해 줍니다.

### 2. 커텀 어드바이저 만들기 (`TimingLoggerAdvisor`)
`CallAdvisor` 인터페이스를 상속받아 AI 통신 시간을 밀리초(ms) 단위로 측정하여 로그로 남기는 클래스입니다.

```java
public class TimingLoggerAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        long startTime = System.currentTimeMillis();
        // ⚠️ chain.nextCall(request)를 반드시 호출해야 다음 단계(AI 응답 요청)로 넘어갑니다.
        ChatClientResponse response = chain.nextCall(request); 
        long endTime = System.currentTimeMillis();
        log.info("[TimingLoggerAdvisor] 종료 : {} ms", endTime - startTime);
        return response;
    }
}
```

---

## 💾 5단계. 대화 이력 저장 (Chat Memory)

대화의 문맥(Context)을 기억하게 하기 위해, 세션 ID별로 대화 이력을 데이터베이스에 영구 보관하고 로드합니다.

Spring AI의 `MessageChatMemoryAdvisor`는 AI를 호출할 때 대화 ID(`conversationId`)를 파라미터로 넘겨주면 이전 대화 기록을 자동으로 LLM에 전달하고, 답변을 받으면 대화 기록을 자동으로 저장해 줍니다.

```java
// ChatClient 호출 시 대화 세션 ID 지정
chatClient.prompt()
    .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, dto.conversationId()))
    .user(dto.message())
    .call().content();
```

실습에서는 아래 3가지 저장소 인터페이스를 구현하여 교체 적용해 보았습니다.

### 📂 구현방식 비교

| 방식 | 설명 및 특징 | 주요 클래스 |
| :--- | :--- | :--- |
| **In-Memory** | 메모리 상에 대화 정보를 임시 보관합니다. WAS 재시작 시 정보가 날아갑니다. | `InMemoryChatMemoryRepository` |
| **MyBatis** | MyBatis XML 매퍼를 이용해 MySQL DB 테이블(`chat_message`)에 대화 기록을 저장/불러오기 합니다. | `MyBatisChatMemoryRepository` |
| **JPA** | Spring Data JPA 기술을 사용해 테이블을 객체 엔티티(`ChatMessageJPA`)로 맵핑하고 영구 보관합니다. | `JpaChatMemoryRepository` |

---

## 🚀 실행하기

1. MySQL 데이터베이스를 기동하고 `schema-mysql.sql` 스크립트를 사용하여 `chat_message` 테이블을 생성합니다. (JPA의 `spring.jpa.hibernate.ddl-auto=create` 옵션 활성화 시 자동 생성됩니다.)
2. 본인 디렉토리의 `.env.dev` 파일에 실제 Groq API Key 및 데이터베이스 커넥션 설정 정보를 기입합니다.
3. Maven 빌드를 수행하여 애플리케이션을 가동시킵니다.
    ```bash
    mvn spring-boot:run
    ```
4. 웹 브라우저에서 `http://localhost:8080/`에 접속하여 질문을 던져보고, 이전 대화 내용이 계속 기억되는지와 데이터베이스에 저장되는지 확인해 봅니다.
