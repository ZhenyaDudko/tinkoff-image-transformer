package com.app.repository;

import com.app.model.FilterSubtask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilterSubtaskRepository extends
        JpaRepository<FilterSubtask, FilterSubtask.CompositeId> {

}
