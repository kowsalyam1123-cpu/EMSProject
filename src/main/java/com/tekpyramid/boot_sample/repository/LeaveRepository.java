package com.tekpyramid.boot_sample.repository;

import com.tekpyramid.boot_sample.entity.Leave;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Integer> {

    @EntityGraph(attributePaths = "employee")
    List<Leave> findByEmployeeId(Integer employeeId);

    @EntityGraph(attributePaths = "employee")
    Page<Leave> findByEmployee_NameContainingIgnoreCase(
            String search,
            Pageable pageable
    );
}