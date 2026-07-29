<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<html>
<head>
    <title>나는 Render가 밉다</title>
</head>
<body>
<h1>Spring AI</h1>
<section>
    <form method="post">
        <input name="message" placeholder="질문을 입력해주세요">
        <button>질문하기</button>
    </form>
</section>
<c:if test="${not empty result}">
    <section>
        <p>답변 :</p>
        <div id="result-raw" hidden><c:out value="${result}"/></div>
        <div id="result-rendered"></div>
    </section>
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const raw = document.querySelector('#result-raw');
            const rendered = document.querySelector('#result-rendered');
            if (raw && rendered) {
                rendered.innerHTML = marked.parse(raw.textContent);
            }
        });
    </script>
</c:if>
</body>
</html>