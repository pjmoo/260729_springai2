CREATE TABLE IF NOT EXISTS chat_message
(
    conversation_id VARCHAR(64) NOT NULL,
    message_type    VARCHAR(20) NOT NULL,
    content         TEXT        NOT NULL,
    seq             INT PRIMARY KEY
);