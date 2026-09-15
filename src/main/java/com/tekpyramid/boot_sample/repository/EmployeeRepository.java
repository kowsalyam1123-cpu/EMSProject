package com.tekpyramid.boot_sample.repository;

import com.tekpyramid.boot_sample.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

public interface EmployeeRepository
        extends JpaRepository<Employee, Integer> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, int id);

    Page<Employee> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String name,
            String email,
            Pageable pageable
    );

//    @Query("""
//            SELECT DISTINCT e
//            FROM Employee e
//            LEFT JOIN FETCH e.department
//            """)
//    List<Employee> findAllWithDepartment();

    @EntityGraph(attributePaths = "department")
    List<Employee> findAll();
}