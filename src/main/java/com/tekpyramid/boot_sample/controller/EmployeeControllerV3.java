package com.tekpyramid.boot_sample.controller;

import com.tekpyramid.boot_sample.dto.EmployeeRequestV3;
import com.tekpyramid.boot_sample.dto.ResponseDto;
import com.tekpyramid.boot_sample.services.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v3/employees")
public class EmployeeControllerV3 {
    private static final Logger log = LoggerFactory.getLogger(EmployeeControllerV3.class);
    private final EmployeeService employeeService;

    public EmployeeControllerV3(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseDto> createEmployee(@RequestPart("employee") EmployeeRequestV3 dto, @RequestPart(value = "file", required = false) MultipartFile file) {
        log.info("Request received to create employee with file");
        if (file != null) {
            log.info("File received: name={}, size={}, contentType={}", file.getOriginalFilename(), file.getSize(), file.getContentType());
        } else {
            log.info("No file uploaded");
        }
        ResponseDto response = employeeService.createEmployeeV3(dto, file);
        log.info("Employee created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    } // noRollbackFor testing api

    @GetMapping("/test-no-rollback")
    public String testNoRollback() {
        log.info("Request received for noRollbackFor test");
        employeeService.testNoRollback();
        log.info("noRollbackFor test completed successfully");
        return "Success";
    }
}