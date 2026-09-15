package com.tekpyramid.boot_sample.services;

import com.tekpyramid.boot_sample.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto saveEmployee(EmployeeRequestDto employeeReqDto);

    EmployeeResponseDto getEmployee(int id);

    List<EmployeeResponseDto> getAllEmployeeV1();

    // V2 Pagination
    EmployeePageResponse getAllEmployee(
            String search,
            Pageable pageable
    );

    EmployeeResponseDto updateEmployee(
            int id,
            EmployeeRequestDto employeeReqDto
    );

    EmployeeResponseDto patchEmployee(
            int id,
            EmployeeRequestDto employeeReqDto
    );

    void deleteEmployee(int id);

    EmployeeResponseDto assignDepartment(
            Integer employeeId,
            Integer departmentId
    );

    EmployeeResponseDto saveEmployeeDetails(
            EmployeeRequestDto employeeReqDto
    );


    ResponseDto createEmployeeV3(EmployeeRequestV3 dto, MultipartFile file);


//noRollbackFor testing method
    void testNoRollback();
}