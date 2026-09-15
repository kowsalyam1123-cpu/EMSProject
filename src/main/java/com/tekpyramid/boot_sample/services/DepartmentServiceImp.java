package com.tekpyramid.boot_sample.services;

import com.tekpyramid.boot_sample.dto.DepartmentRequestDto;
import com.tekpyramid.boot_sample.dto.DepartmentResponseDto;
import com.tekpyramid.boot_sample.entity.Department;
import com.tekpyramid.boot_sample.exception.DataNotFoundException;
import com.tekpyramid.boot_sample.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImp implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImp(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public DepartmentResponseDto saveDepartment(DepartmentRequestDto dto) {
        if (departmentRepository.existsByDepartmentNameIgnoreCase(dto.getDepartmentName())) {
            throw new IllegalArgumentException("Department already exists");
        }

        Department department = new Department();
        department.setDepartmentName(dto.getDepartmentName().trim());

        Department saved = departmentRepository.save(department);
        return new DepartmentResponseDto(saved.getId(), saved.getDepartmentName());
    }

    @Override
    public DepartmentResponseDto getDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Department Not Found"));

        return new DepartmentResponseDto(
                department.getId(),
                department.getDepartmentName());
    }

    @Override
    public List<DepartmentResponseDto> getAllDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(department -> new DepartmentResponseDto(
                        department.getId(),
                        department.getDepartmentName()))
                .toList();
    }

    @Override
    public void deleteDepartment(Integer id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Department Not Found"));

        departmentRepository.delete(department);
    }
}
