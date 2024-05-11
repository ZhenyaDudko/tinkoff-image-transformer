package com.app.kafka;

import com.app.filter.BlurFilter;
import com.app.filter.EdgeDetectionFilter;
import com.app.filter.FilterFunction;
import com.app.filter.GrayscaleFilter;
import com.app.model.FilterSubtask;
import com.app.repository.FilterSubtaskRepository;
import com.app.service.ImageService;
import com.app.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.app.kafka.KafkaInitializer.TOPIC_WIP;

@Component
@RequiredArgsConstructor
public class KafkaWipConsumers {

    /**
     * Repository for idempotency checks.
     */
    private final FilterSubtaskRepository filterSubtaskRepository;

    /**
     * Producer for images.wip topic.
     */
    private final KafkaWipSender kafkaWipSender;

    /**
     * Producer for images.done topic.
     */
    private final KafkaDoneSender kafkaDoneSender;

    /**
     * Minio service.
     */
    private final MinioService minioService;

    /**
     * Blur request listener.
     *
     * @param record         received message.
     * @param acknowledgment object for pushing offset.
     */
    @KafkaListener(
            topics = TOPIC_WIP,
            groupId = "consumer-wip-blur",
            concurrency = "2",
            properties = {
                    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
                    ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
                    ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG
                      + "=org.apache.kafka.clients.consumer.RoundRobinAssignor"
            }
    )
    public void consumeBlur(
            final ConsumerRecord<String, ImageWipMessage> record,
            final Acknowledgment acknowledgment
    ) throws Exception {
        consume(record,
                acknowledgment,
                ImageService.Filter.BLUR,
                BlurFilter::applyFilter
        );
    }

    /**
     * Grayscale request listener.
     *
     * @param record         received message.
     * @param acknowledgment object for pushing offset.
     */
    @KafkaListener(
            topics = TOPIC_WIP,
            groupId = "consumer-wip-grayscale",
            concurrency = "2",
            properties = {
                    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
                    ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
                    ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG
                      + "=org.apache.kafka.clients.consumer.RoundRobinAssignor"
            }
    )
    public void consumeGrayscale(
            final ConsumerRecord<String, ImageWipMessage> record,
            final Acknowledgment acknowledgment
    ) throws Exception {
        consume(record,
                acknowledgment,
                ImageService.Filter.GRAYSCALE,
                GrayscaleFilter::applyFilter
        );
    }

    /**
     * EdgeDetection request listener.
     *
     * @param record         received message.
     * @param acknowledgment object for pushing offset.
     */
    @KafkaListener(
            topics = TOPIC_WIP,
            groupId = "consumer-wip-edge",
            concurrency = "2",
            properties = {
                    ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG + "=false",
                    ConsumerConfig.ISOLATION_LEVEL_CONFIG + "=read_committed",
                    ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG
                      + "=org.apache.kafka.clients.consumer.RoundRobinAssignor"
            }
    )
    public void consumeEdge(
            final ConsumerRecord<String, ImageWipMessage> record,
            final Acknowledgment acknowledgment
    ) throws Exception {
        consume(record,
                acknowledgment,
                ImageService.Filter.EDGES,
                EdgeDetectionFilter::applyFilter
        );
    }

    private void consume(
            final ConsumerRecord<String, ImageWipMessage> record,
            final Acknowledgment acknowledgment,
            final ImageService.Filter filter,
            final FilterFunction filterWorker
    ) throws Exception {
        List<ImageService.Filter> filters = record.value().getFilters();
        String requestId = record.value().getRequestId();
        String imageId = record.value().getImageId();
        String mediaType = record.value().getMediaType();

        if (filters.isEmpty()
                || filters.get(0) != filter
                || filterSubtaskRepository
                .existsById(new FilterSubtask.CompositeId(requestId, imageId))
        ) {
            acknowledgment.acknowledge();
            return;
        }

        byte[] image = minioService.downloadImage(imageId);
        byte[] resultImage = filterWorker.applyFilter(image, mediaType);
        String newImageId;
        if (filters.size() == 1) {
            newImageId = minioService.uploadImage(resultImage, mediaType);
        } else {
            newImageId = minioService.uploadImage(
                    resultImage,
                    mediaType,
                    "intermediate/"
            );
        }
        filterSubtaskRepository.save(new FilterSubtask(
                new FilterSubtask.CompositeId(requestId, imageId))
        );
        if (filters.size() == 1) {
            kafkaDoneSender.sendMessage(new ImageDoneMessage(
                    newImageId,
                    requestId)
            );
        } else {
            kafkaWipSender.sendMessage(new ImageWipMessage(
                            newImageId,
                            requestId,
                            mediaType,
                            filters.subList(1, filters.size())
            ));
        }
        acknowledgment.acknowledge();
    }

}
