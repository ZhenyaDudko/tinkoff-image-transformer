package com.app.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.app.kafka.KafkaInitializer.TOPIC_WIP;

@Component
public class KafkaWipSender {

    /**
     * Kafka producer.
     */
    private final KafkaTemplate<String, ImageWipMessage> template;

    /**
     * Constructor.
     * @param allAcksKafkaTemplate - kafka producer
     */
    public KafkaWipSender(
            final KafkaTemplate<String, ImageWipMessage> allAcksKafkaTemplate
    ) {
        template = allAcksKafkaTemplate;
    }

    /**
     * Send message into images.wip topic.
     * @param message message
     */
    public void sendMessage(final ImageWipMessage message) {
        template.send(TOPIC_WIP, message);
    }

}
