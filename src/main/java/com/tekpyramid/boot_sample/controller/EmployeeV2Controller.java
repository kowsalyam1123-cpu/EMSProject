package com.tekpyramid.boot_sample.controller;

import com.tekpyramid.boot_sample.dto.EmployeePageResponse;
import com.tekpyramid.boot_sample.services.EmployeeService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/employee")
public class EmployeeV2Controller {

    private final EmployeeService employeeService;

    public EmployeeV2Controller(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public EmployeePageResponse getAllEmployee(
            @RequestParam(required = false) String search,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            )
            Pageable pageable) {

        return employeeService.getAllEmployee(search, pageable);
    }
}