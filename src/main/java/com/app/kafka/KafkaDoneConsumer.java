package com.app.kafka;

import com.app.model.FilterQuery;
import com.app.model.ImageMeta;
import com.app.repository.FilterQueryRepository;
import com.app.repository.ImageMetaRepository;
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
     * Image meta repository.
     */
    private final ImageMetaRepository imageMetaRepository;

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
        String finalImageId = record.value().getImageId();
        String requestId = record.value().getRequestId();

        Optional<FilterQuery> filterQuery = filterQueryRepository
                .findByRequestId(requestId);
        filterQuery.ifPresent(query -> {
            Optional<ImageMeta> imageMeta = imageMetaRepository
                    .findImageMetaByImageId(query.getImageId());
            imageMeta.ifPresent(meta -> {
                imageMetaRepository.save(new ImageMeta()
                        .setImageId(finalImageId)
                        .setUserId(meta.getUserId())
                        .setMediaType(meta.getMediaType())
                        .setSize(meta.getSize())
                        .setName(meta.getName() + "-filtered-" + requestId)
                );
                filterQueryRepository.save(query
                        .setStatus(FilterQuery.Status.DONE)
                        .setFilteredImageId(finalImageId));
            });
        });
        acknowledgment.acknowledge();
    }

}
