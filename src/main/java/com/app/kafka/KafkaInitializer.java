package com.app.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;
import java.util.function.Consumer;


@Configuration
@RequiredArgsConstructor
public class KafkaInitializer {
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
    private static final int PARTITIONS = 1;

    /**
     * Number of replicas.
     */
    private static final short REPLICAS = 1;

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
     * Create bean for images.wip producer.
     * @return producer
     */
    @Bean
    public KafkaTemplate<String, ImageWipMessage> imagesWipProducer() {
        return new KafkaTemplate<>(producerFactory(props ->
                props.put(ProducerConfig.ACKS_CONFIG, "all"))
        );
    }

    /**
     * Create bean for images.done producer.
     * @return producer
     */
    @Bean
    public KafkaTemplate<String, ImageDoneMessage> imagesDoneProducer() {
        return new KafkaTemplate<>(producerFactory(props ->
                props.put(ProducerConfig.ACKS_CONFIG, "all"))
        );
    }

    private <V> ProducerFactory<String, V> producerFactory(
            final Consumer<Map<String, Object>> enchanter
    ) {
        var props = properties.buildProducerProperties(null);

        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class
        );
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class
        );

        props.put(
                ProducerConfig.PARTITIONER_CLASS_CONFIG,
                RoundRobinPartitioner.class
        );

        props.put(ProducerConfig.LINGER_MS_CONFIG, 0);

        props.put(
                ProducerConfig.RETRIES_CONFIG,
                Integer.toString(Integer.MAX_VALUE)
        );

        enchanter.accept(props);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Consumer factory bean.
     * @param <V>
     * @return Consumer factory bean.
     */
    @Bean
    public <V> ConsumerFactory<String, V> consumerFactory() {
        var props = properties.buildConsumerProperties(null);
        JsonDeserializer<V> ds = new JsonDeserializer<>();
        ds.addTrustedPackages("*");
        return new DefaultKafkaConsumerFactory<>(props,
                new StringDeserializer(),
                ds);
    }
}
