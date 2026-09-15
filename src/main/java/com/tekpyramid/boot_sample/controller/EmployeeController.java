package com.tekpyramid.boot_sample.controller;

import com.tekpyramid.boot_sample.dto.EmployeeRequestDto;
import com.tekpyramid.boot_sample.dto.EmployeeResponseDto;
import com.tekpyramid.boot_sample.dto.ResponseDto;
import com.tekpyramid.boot_sample.services.EmployeeService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
public class EmployeeController {

    private static final Logger log =
            LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping("/employee")

    public EmployeeResponseDto saveEmployee(@Valid @RequestBody EmployeeRequestDto employeeReqDto) {

        log.debug("Employee request: {}", employeeReqDto);
        return employeeService.saveEmployee(employeeReqDto);

//        Setter used
//        ResponseDto responseDto = new ResponseDto();
//        responseDto.setError(false);
//        responseDto.setMessage("Employee created successfully");
//        responseDto.setData(employeeService.saveEmployee(employeeReqDto));
//        return responseDto;

//        Constructor is used
//        return new ResponseDto(false, "Employee created successfully",
//        employeeService.saveEmployee(employeeReqDto));

//        Builder is used
//        return ResponseDto.builder()
//                .error(false)
//                .message("Employee created successfully")
//                .data(employeeService.saveEmployee(employeeReqDto))
//                .build();
    }

    @GetMapping("/employee/{id}")
    public ResponseDto getEmployee(@PathVariable int id) {
        log.info("Request received to fetch employee with id: {}", id);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setError(false);
        responseDto.setMessage("Employee fetch successfully");
        responseDto.setData(employeeService.getEmployee(id));
        log.info("Employee fetched successfully with id: {}", id);
        return responseDto;
    }


    //    Task 3 pageable
    @GetMapping("/employees")
    public ResponseDto getAllEmployee() {
        log.info("Request received to fetch all employees");
        ResponseDto responseDto = new ResponseDto();
        responseDto.setError(false);
        responseDto.setMessage("Employee fetch all successfully");
        List<EmployeeResponseDto> employees = employeeService.getAllEmployeeV1();
        responseDto.setData(employees);
        log.info("Total employees fetched: {}", employees.size());
        return responseDto;
    }

    @PutMapping("employee/updateall/{id}")
    public EmployeeResponseDto updateEmployee(@Valid @PathVariable int id, @RequestBody EmployeeRequestDto employeeReqDto) {
        log.info("Request received to update employee with id: {}", id);
        EmployeeResponseDto response = employeeService.updateEmployee(id, employeeReqDto);
        log.info("Employee updated successfully with id: {}", id);
        return response;
    }

    @PatchMapping("employee/update/{id}")
    public EmployeeResponseDto patchEmployee(@Valid @PathVariable int id, @RequestBody EmployeeRequestDto employeeReqDto) {
        log.info("Request received to partially update employee with id: {}", id);
        EmployeeResponseDto response = employeeService.patchEmployee(id, employeeReqDto);
        log.info("Employee partially updated successfully with id: {}", id);
        return response;
    }

    @DeleteMapping("employee/delete/{id}")
    public String deleteEmployee(@PathVariable int id) {
        log.info("Request received to delete employee with id: {}", id);
        employeeService.deleteEmployee(id);
        log.info("Employee deleted successfully with id: {}", id);
        return "Employee deleted successfully";
    }

    // Task 1 assign the department for employee
    @PutMapping("/employee/{employeeId}/department/{departmentId}")
    public EmployeeResponseDto assignDepartment(@PathVariable Integer employeeId, @PathVariable Integer departmentId) {
        log.info("Request received to assign department. Employee ID: {}, Department ID: {}", employeeId, departmentId);
        EmployeeResponseDto response = employeeService.assignDepartment(employeeId, departmentId);
        log.info("Department assigned successfully. Employee ID: {}, Department ID: {}", employeeId, departmentId);
        return response;
    }

    // Task 2 api v2
    @PostMapping("/employees/details")
    public ResponseDto saveEmployeeDetails(@Valid @RequestBody EmployeeRequestDto employeeReqDto) {
        log.info("Request received to save employee details");
        ResponseDto responseDto = new ResponseDto();
        responseDto.setError(false);
        responseDto.setMessage("Employee details saved successfully");
        responseDto.setData(employeeService.saveEmployeeDetails(employeeReqDto));
        log.info("Employee details saved successfully");
        return responseDto;
    }


}
