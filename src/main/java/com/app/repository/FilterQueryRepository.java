package com.app.repository;

import com.app.model.FilterQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilterQueryRepository extends
        JpaRepository<FilterQuery, Integer> {
    /**
     * Find filter query by image id.
     *
     * @param requestId request id.
     * @return optional FilterQuery entity.
     */
    Optional<FilterQuery> findByRequestId(String requestId);
}
