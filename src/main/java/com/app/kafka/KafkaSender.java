package com.app.kafka;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.app.kafka.KafkaInitializer.ALL_ACKS_KAFKA_TEMPLATE;


@Component
public class KafkaSender {

    private final KafkaTemplate<String, ImageWipMessage> template;

    public static final String TOPIC_WIP = "images.wip";
    public static final String TOPIC_DONE = "images.done";

    public KafkaSender(
            @Qualifier(ALL_ACKS_KAFKA_TEMPLATE)
            KafkaTemplate<String, ImageWipMessage> allAcksKafkaTemplate
    ) {
        template = allAcksKafkaTemplate;
    }

    public void sendMessage(ImageWipMessage message) {
        template.send(TOPIC_WIP, message);
    }

}
