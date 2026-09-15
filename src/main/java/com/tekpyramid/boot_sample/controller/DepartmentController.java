package com.tekpyramid.boot_sample.controller;

import com.tekpyramid.boot_sample.dto.DepartmentRequestDto;
import com.tekpyramid.boot_sample.dto.DepartmentResponseDto;
import com.tekpyramid.boot_sample.services.DepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/department")
public class DepartmentController {

    private static final Logger log =
            LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/department/save")
    public DepartmentResponseDto saveDepartment(
            @RequestBody DepartmentRequestDto dto) {

        log.info("Request received to save department: {}", dto);

        DepartmentResponseDto response =
                departmentService.saveDepartment(dto);

        log.info("Department saved successfully: {}", response);

        return response;
    }

    @GetMapping("/department/fetch/{id}")
    public DepartmentResponseDto getDepartment(
            @PathVariable Integer id) {

        log.info("Request received to fetch department with id: {}", id);

        DepartmentResponseDto response =
                departmentService.getDepartment(id);

        log.info("Department fetched successfully for id: {}", id);

        return response;
    }

    @GetMapping("/department/fetchall")
    public List<DepartmentResponseDto> getAllDepartments() {

        log.info("Request received to fetch all departments");

        List<DepartmentResponseDto> departments =
                departmentService.getAllDepartments();

        log.info("Total departments fetched: {}", departments.size());

        return departments;
    }

    @DeleteMapping("/department/delete/{id}")
    public String deleteDepartment(
            @PathVariable Integer id) {

        log.info("Request received to delete department with id: {}", id);

        departmentService.deleteDepartment(id);

        log.info("Department deleted successfully with id: {}", id);

        return "Department Deleted Successfully";
    }
}
