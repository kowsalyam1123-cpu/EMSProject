package com.tekpyramid.boot_sample.repository;

import com.tekpyramid.boot_sample.entity.Department;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository
        extends JpaRepository<Department, Integer> {

    Optional<Department> findByDepartmentName(String departmentName);

    boolean existsByDepartmentNameIgnoreCase(@NotBlank(message = "Department name is required") String departmentName);

    Optional<Department> findByDepartmentNameIgnoreCase(@NotBlank(message = "Department name is required") String departmentName);
}