package com.app.kafka;

import com.app.model.FilterQuery;
import com.app.repository.FilterQueryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.app.kafka.KafkaInitializer.TOPIC_DONE;

@Component
@RequiredArgsConstructor
public class KafkaDoneConsumer {

    /**
     * Filter query repository.
     */
    private final FilterQueryRepository filterQueryRepository;

    /**
     * Listener.
     *
     * @param record         received message.
     * @param acknowledgment object for pushing offset.
     */
    @KafkaListener(
            topics = TOPIC_DONE,
            groupId = "consumer-done",
            concurrency = "2",
            properties = {
                    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
                    ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
                    ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG
                      + "=org.apache.kafka.clients.consumer.RoundRobinAssignor"
            }
    )
    public void consume(
            final ConsumerRecord<String, ImageDoneMessage> record,
            final Acknowledgment acknowledgment
    ) {
        Optional<FilterQuery> filterQuery = filterQueryRepository
                .findByRequestId(record.value().getRequestId());
        filterQuery.ifPresent(query -> filterQueryRepository.save(query
                .setStatus(FilterQuery.Status.DONE)
                .setFilteredImageId(record.value().getImageId())
        ));
        acknowledgment.acknowledge();
    }

}
