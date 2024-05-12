package com.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "filter-subtask")
@AllArgsConstructor
@NoArgsConstructor
public class FilterSubtask {

    /**
     * Composite id.
     */
    @EmbeddedId
    private CompositeId id;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class CompositeId implements Serializable {

        /**
         * Filter request id.
         */
        @Column(nullable = false)
        private String requestId;

        /**
         * Image id.
         */
        @Column(nullable = false)
        private String imageId;
    }
}
