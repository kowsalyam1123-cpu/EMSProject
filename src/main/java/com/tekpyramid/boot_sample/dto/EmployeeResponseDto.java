package com.tekpyramid.boot_sample.dto;

import com.tekpyramid.boot_sample.entity.EmployeeAddress;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDto {

    private String name;
    private String email;
    private EmployeeAddress address;
    private DepartmentResponseDto department;
}
