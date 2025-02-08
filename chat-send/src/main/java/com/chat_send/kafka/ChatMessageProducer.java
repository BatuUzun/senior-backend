package com.chat_send.kafka;

import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import com.chat_send.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ChatMessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final Logger logger = LoggerFactory.getLogger(ChatMessageProducer.class);

    public ChatMessageProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Retryable(
        value = KafkaException.class,
        maxAttempts = Constants.MAX_ATTEMPTS, // Retry 
        backoff = @Backoff(delay = Constants.DELAY_IN_MILISECONDS) // Wait before retrying
    )
    public void sendMessage(String topic, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                logger.info("✅ Kafka message sent: {}", message);
            } else {
                logger.error("❌ Kafka message failed: {}", exception.getMessage(), exception);
            }
        });
    }
}
