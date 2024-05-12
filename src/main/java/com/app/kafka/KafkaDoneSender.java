package com.app.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.app.kafka.KafkaInitializer.TOPIC_DONE;

@Component
public class KafkaDoneSender {

    /**
     * Kafka producer.
     */
    private final KafkaTemplate<String, ImageDoneMessage> template;

    /**
     * Constructor.
     * @param allAcksKafkaTemplate - kafka producer
     */
    public KafkaDoneSender(
            final KafkaTemplate<String, ImageDoneMessage> allAcksKafkaTemplate
    ) {
        template = allAcksKafkaTemplate;
    }

    /**
     * Send message into images.wip topic.
     * @param message message
     */
    public void sendMessage(final ImageDoneMessage message) {
        template.send(TOPIC_DONE, message);
    }

}
