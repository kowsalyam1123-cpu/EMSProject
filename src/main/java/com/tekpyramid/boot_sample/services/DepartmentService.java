package com.tekpyramid.boot_sample.services;

import com.tekpyramid.boot_sample.dto.DepartmentRequestDto;
import com.tekpyramid.boot_sample.dto.DepartmentResponseDto;

import java.util.List;

public interface DepartmentService {

    DepartmentResponseDto saveDepartment(DepartmentRequestDto departmentRequestDto);

    DepartmentResponseDto getDepartment(Integer id);

    List<DepartmentResponseDto> getAllDepartments();

    void deleteDepartment(Integer id);
}
