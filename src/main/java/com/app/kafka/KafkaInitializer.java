package com.app.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.function.Consumer;


@Configuration
@RequiredArgsConstructor
public class KafkaInitializer {
    /**
     * Qualified name for all asks kafka producer.
     */
    public static final String ALL_ACKS_KAFKA_TEMPLATE =
            "allAcksKafkaTemplate";

    /**
     * Topic for not filtered images.
     */
    public static final String TOPIC_WIP = "images.wip";

    /**
     * Topic for filtered images.
     */
    public static final String TOPIC_DONE = "images.done";

    /**
     * Number of partitions.
     */
    private static final int PARTITIONS = 2;

    /**
     * Number of replicas.
     */
    private static final short REPLICAS = 3;

    /**
     * Kafka properties.
     */
    private final KafkaProperties properties;

    /**
     * Create bean for images.wip topic.
     * @return topic
     */
    @Bean
    public NewTopic topicImagesWip() {
        return new NewTopic(TOPIC_WIP, PARTITIONS, REPLICAS);
    }

    /**
     * Create bean for images.done topic.
     * @return topic
     */
    @Bean
    public NewTopic topicImagesDone() {
        return new NewTopic(TOPIC_DONE, PARTITIONS, REPLICAS);
    }

    /**
     * Create bean for all asks kafka producer.
     * @return producer
     */
    @Bean(ALL_ACKS_KAFKA_TEMPLATE)
    public KafkaTemplate<String, ImageWipMessage> allAcksKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(props ->
                props.put(ProducerConfig.ACKS_CONFIG, "all"))
        );
    }

    private ProducerFactory<String, ImageWipMessage> producerFactory(
            final Consumer<Map<String, Object>> enchanter
    ) {
        var props = properties.buildProducerProperties(null);

        // Работаем со строками
        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        // Партиция одна, так что все равно как роутить
        props.put(
                ProducerConfig.PARTITIONER_CLASS_CONFIG,
                RoundRobinPartitioner.class
        );

        // Отправляем сообщения сразу
        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);

        // 1 попытка на все про все
        props.put(
                ProducerConfig.RETRIES_CONFIG,
                Integer.toString(Integer.MAX_VALUE)
        );

        // До-обогащаем конфигурацию
        enchanter.accept(props);

        return new DefaultKafkaProducerFactory<>(props);
    }

}
