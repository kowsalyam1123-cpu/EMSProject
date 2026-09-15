package com.tekpyramid.boot_sample.util;

import com.tekpyramid.boot_sample.dto.DepartmentResponseDto;
import com.tekpyramid.boot_sample.dto.EmployeeRequestDto;
import com.tekpyramid.boot_sample.dto.EmployeeResponseDto;
import com.tekpyramid.boot_sample.entity.Department;
import com.tekpyramid.boot_sample.entity.Employee;
import com.tekpyramid.boot_sample.entity.EmployeeAddress;
import com.tekpyramid.boot_sample.exception.DataNotFoundException;
import com.tekpyramid.boot_sample.repository.DepartmentRepository;

public class EmployeeMapper {

    public static EmployeeResponseDto entitytoDto(Employee employee) {
        EmployeeResponseDto dto = new EmployeeResponseDto();

        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());

        if (employee.getAddresses() != null) {
            EmployeeAddress address = new EmployeeAddress();
            address.setAd_id(employee.getAddresses().getAd_id());
            address.setCity(employee.getAddresses().getCity());
            address.setState(employee.getAddresses().getState());
            address.setCountry(employee.getAddresses().getCountry());
            dto.setAddress(address);
        }

        if (employee.getDepartment() != null) {
            DepartmentResponseDto departmentDto = new DepartmentResponseDto();
            departmentDto.setId(employee.getDepartment().getId());
            departmentDto.setDepartmentName(employee.getDepartment().getDepartmentName());
            dto.setDepartment(departmentDto);
        }

        return dto;
    }

    public static Employee dtoToEntity(
            EmployeeRequestDto dto,
            DepartmentRepository departmentRepository) {

        Employee employee = new Employee();

        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setAge(dto.getAge());

        EmployeeAddress address = new EmployeeAddress();
        address.setCity(dto.getAddress().getCity());
        address.setState(dto.getAddress().getState());
        address.setCountry(dto.getAddress().getCountry());
        employee.setAddresses(address);

        Department department = departmentRepository
                .findByDepartmentNameIgnoreCase(dto.getDepartment().getDepartmentName())
                .orElseGet(() -> {
                    Department newDepartment = new Department();
                    newDepartment.setDepartmentName(dto.getDepartment().getDepartmentName().trim());
                    return departmentRepository.save(newDepartment);
                });

        employee.setDepartment(department);

        return employee;
    }

    public static void updateAddress(Employee employee, EmployeeRequestDto dto) {
        if (dto.getAddress() == null) {
            return;
        }

        EmployeeAddress address = employee.getAddresses();

        if (address == null) {
            address = new EmployeeAddress();
            employee.setAddresses(address);
        }

        if (dto.getAddress().getCity() != null) {
            address.setCity(dto.getAddress().getCity());
        }
        if (dto.getAddress().getState() != null) {
            address.setState(dto.getAddress().getState());
        }
        if (dto.getAddress().getCountry() != null) {
            address.setCountry(dto.getAddress().getCountry());
        }
    }

    public static void updateDepartment(
            Employee employee,
            EmployeeRequestDto dto,
            DepartmentRepository departmentRepository) {

        if (dto.getDepartment() == null) {
            return;
        }

        Department department = departmentRepository
                .findByDepartmentNameIgnoreCase(dto.getDepartment().getDepartmentName())
                .orElseGet(() -> {
                    Department newDepartment = new Department();
                    newDepartment.setDepartmentName(
                            dto.getDepartment().getDepartmentName().trim());
                    return departmentRepository.save(newDepartment);
                });

        employee.setDepartment(department);
    }


}
