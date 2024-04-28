package com.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "filter-query")
public class FilterQuery {

    /**
     * Id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Request id in system.
     */
    private String requestId;

    /**
     * Image original name.
     */
    private Status status;

    /**
     * Image id in storage.
     */
    private String imageId;

    /**
     * Filtered image id in storage.
     */
    private String filteredImageId;

    public enum Status {
        WIP, DONE
    }
}
