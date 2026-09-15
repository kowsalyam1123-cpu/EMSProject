package com.tekpyramid.boot_sample.repository;

import com.tekpyramid.boot_sample.entity.EmployeeDocuments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDocumentsRepository
        extends JpaRepository<EmployeeDocuments, Integer> {
}